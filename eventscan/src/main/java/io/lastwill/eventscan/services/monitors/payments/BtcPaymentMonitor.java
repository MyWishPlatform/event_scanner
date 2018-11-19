package io.lastwill.eventscan.services.monitors.payments;

import io.lastwill.eventscan.events.model.ProductPaymentEvent;
import io.lastwill.eventscan.events.model.UserPaymentEvent;
import io.lastwill.eventscan.model.CryptoCurrency;
import io.lastwill.eventscan.model.NetworkType;
import io.lastwill.eventscan.repositories.ProductRepository;
import io.lastwill.eventscan.repositories.UserSiteBalanceRepository;
import io.lastwill.eventscan.services.Btc2RskNetworkConverter;
import io.mywish.blockchain.WrapperOutput;
import io.mywish.blockchain.WrapperTransaction;
import io.mywish.scanner.model.NewBlockEvent;
import io.mywish.scanner.services.EventPublisher;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Set;
import java.util.stream.IntStream;

@Slf4j
@Component
public class BtcPaymentMonitor {
    @Autowired
    private UserSiteBalanceRepository userSiteBalanceRepository;
    @Autowired
    private EventPublisher eventPublisher;

    @EventListener
    private void handleBtcBlock(NewBlockEvent event) {
        if (event.getNetworkType() != NetworkType.BTC_MAINNET) {
            return;
        }
        Set<String> addresses = event.getTransactionsByAddress().keySet();
        if (addresses.isEmpty()) {
            return;
        }
        userSiteBalanceRepository.findByBtcAddressesList(addresses)
                .forEach(userSiteBalance -> {
                    List<WrapperTransaction> txes = event.getTransactionsByAddress().get(userSiteBalance.getBtcAddress());
                    if (txes == null) {
                        log.warn("There is no UserSiteBalance entity found for BTC address {}.", userSiteBalance.getBtcAddress());
                        return;
                    }
                    for (WrapperTransaction tx: txes) {
                        for (WrapperOutput output: tx.getOutputs()) {
                            if (output.getParentTransaction() == null) {
                                log.warn("Skip it. Output {} has not parent transaction.", output);
                                continue;
                            }
                            if (!output.getAddress().equalsIgnoreCase(userSiteBalance.getBtcAddress())) {
                                continue;
                            }

                            eventPublisher.publish(new UserPaymentEvent(
                                    event.getNetworkType(),
                                    tx,
                                    output.getValue(),
                                    CryptoCurrency.BTC,
                                    true,
                                    userSiteBalance
                            ));
                        }
                    }
                });
    }
}
