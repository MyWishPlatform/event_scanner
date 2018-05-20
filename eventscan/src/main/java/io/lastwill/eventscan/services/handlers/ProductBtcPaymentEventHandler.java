package io.lastwill.eventscan.services.handlers;

import io.lastwill.eventscan.events.ProductPaymentEvent;
import io.lastwill.eventscan.messages.ContractPaymentNotify;
import io.lastwill.eventscan.messages.PaymentStatus;
import io.lastwill.eventscan.services.ExternalNotifier;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.context.PayloadApplicationEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import java.math.BigInteger;

@Slf4j
@Component
public class ProductBtcPaymentEventHandler implements ApplicationListener<PayloadApplicationEvent> {
    @Autowired
    private ExternalNotifier externalNotifier;

    @Override
    public void onApplicationEvent(PayloadApplicationEvent springEvent) {
        Object event = springEvent.getPayload();
        if (event instanceof ProductPaymentEvent) handleBtcBlock((ProductPaymentEvent) event);
    }

    private void handleBtcBlock(ProductPaymentEvent event) {
        if (event.getTransactionOutput().getParentTransaction() == null) {
            log.warn("Skip it. Output {} has not parent transaction.", event.getTransactionOutput());
            return;
        }
        externalNotifier.send(event.getNetworkType(), new ContractPaymentNotify(
                BigInteger.valueOf(event.getTransactionOutput().getValue().value),
                PaymentStatus.COMMITTED,
                event.getTransactionOutput().getParentTransaction().getHashAsString(),
                event.getTransactionOutput().getIndex(),
                event.getProduct()
        ));
    }
}
