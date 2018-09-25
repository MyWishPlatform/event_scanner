package io.lastwill.eventscan.services.monitors.payments;

import io.lastwill.eventscan.events.model.ProductPaymentEvent;
import io.lastwill.eventscan.model.CryptoCurrency;
import io.lastwill.eventscan.repositories.ProductRepository;
import io.lastwill.eventscan.services.Btc2RskNetworkConverter;
import io.lastwill.eventscan.model.NetworkType;
import io.mywish.scanner.model.NewBlockEvent;
import io.mywish.scanner.services.EventPublisher;
import io.mywish.blockchain.WrapperOutput;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import java.util.Set;
import java.util.stream.IntStream;

@Slf4j
@Component
public class BtcPaymentMonitor {
    @Autowired
    private ProductRepository productRepository;
    @Autowired
    private Btc2RskNetworkConverter btc2RskNetworkConverter;
    @Autowired
    private EventPublisher eventPublisher;

    @EventListener
    private void handleBtcBlock(NewBlockEvent event) {
        if (event.getNetworkType() != NetworkType.BTC_MAINNET ||
                event.getNetworkType() != NetworkType.BTC_TESTNET_3) {
            return;
        }
        NetworkType targetNetwork = btc2RskNetworkConverter.convert(event.getNetworkType());
        Set<String> addresses = event.getTransactionsByAddress().keySet();
        if (addresses.isEmpty()) {
            return;
        }
        productRepository.findLastWillByBtcAddresses(addresses, targetNetwork)
                .forEach(productLastWill -> {
                    event.getTransactionsByAddress().get(productLastWill.getBtcKey().getAddress()).forEach(tx -> {
                        IntStream.range(0, tx.getOutputs().size())
                                .forEach(index -> {
                                    WrapperOutput output = tx.getOutputs().get(index);
                                    if (output.getParentTransaction() == null) {
                                        log.warn("Skip it. Output {} has not parent transaction.", output);
                                        return;
                                    }
                                    eventPublisher.publish(new ProductPaymentEvent(
                                            targetNetwork,
                                            null,
                                            productLastWill.getBtcKey().getAddress(),
                                            output.getValue(),
                                            CryptoCurrency.BTC,
                                            true,
                                            productLastWill,
                                            output
                                    ));
                                });
                    });
                });
    }
}
