package io.lastwill.eventscan.services.handlers;

import io.lastwill.eventscan.messages.ContractPaymentNotify;
import io.lastwill.eventscan.messages.PaymentStatus;
import io.lastwill.eventscan.repositories.ProductRepository;
import io.lastwill.eventscan.services.Btc2RskNetworkConverter;
import io.lastwill.eventscan.services.ExternalNotifier;
import io.mywish.scanner.model.NetworkType;
import io.mywish.scanner.model.NewBtcBlockEvent;
import lombok.extern.slf4j.Slf4j;
import org.bitcoinj.core.TransactionOutput;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import java.math.BigInteger;
import java.util.List;
import java.util.Set;
import java.util.stream.IntStream;

@Slf4j
@Component
public class BtcPaymentEventHandler {
    @Autowired
    private ProductRepository productRepository;
    @Autowired
    private ExternalNotifier externalNotifier;
    @Autowired
    private Btc2RskNetworkConverter btc2RskNetworkConverter;

    @EventListener
    public void handleBtcBlock(NewBtcBlockEvent event) {
        NetworkType targetNetwork = btc2RskNetworkConverter.convert(event.getNetworkType());
        Set<String> addresses = event.getAddressTransactionOutputs().keySet();
        if (addresses.isEmpty()) {
            return;
        }
        productRepository.findLastWillByBtcAddresses(addresses, targetNetwork)
                .forEach(productLastWill -> {
                    List<TransactionOutput> outputs = event.getAddressTransactionOutputs().get(productLastWill.getBtcKey().getAddress());
                    IntStream.range(0, outputs.size())
                            .forEach(index -> {
                                TransactionOutput output = outputs.get(index);
                                if (output.getParentTransaction() == null) {
                                    log.warn("Skip it. Output {} has not parent transaction.", output);
                                    return;
                                }
                                externalNotifier.send(targetNetwork, new ContractPaymentNotify(
                                        BigInteger.valueOf(output.getValue().value),
                                        PaymentStatus.COMMITTED,
                                        output.getParentTransaction().getHashAsString(),
                                        index,
                                        productLastWill
                                ));
                            });
                });
    }
}
