package io.lastwill.eventscan.services.handlers;

import io.lastwill.eventscan.messages.ContractPaymentNotify;
import io.lastwill.eventscan.messages.PaymentStatus;
import io.lastwill.eventscan.repositories.ProductRepository;
import io.lastwill.eventscan.services.ExternalNotifier;
import io.mywish.scanner.model.NewBtcBlockEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import java.math.BigInteger;
import java.util.Set;

@Slf4j
@Component
public class BtcPaymentEventHandler {
    @Autowired
    private ProductRepository productRepository;
    @Autowired
    private ExternalNotifier externalNotifier;

    @EventListener
    public void handleBtcBlock(NewBtcBlockEvent event) {
        Set<String> addresses = event.getAddressTransactionOutputs().keySet();
        productRepository.findLastWillByBtcAddresses(addresses, event.getNetworkType())
                .forEach(productLastWill -> {
                    event.getAddressTransactionOutputs().get(productLastWill.getBtcKey().getAddress())
                            .forEach(output -> {
                                if (output.getParentTransaction() == null) {
                                    log.warn("Skip it. Output {} has not parent transaction.", output);
                                    return;
                                }
                                externalNotifier.send(event.getNetworkType(), new ContractPaymentNotify(
                                        BigInteger.valueOf(output.getValue().value),
                                        PaymentStatus.COMMITTED,
                                        output.getParentTransactionHash().toString(),
                                        productLastWill
                                ));
                            });

                });
    }
}
