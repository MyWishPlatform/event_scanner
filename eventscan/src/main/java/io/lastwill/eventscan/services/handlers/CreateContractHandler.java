package io.lastwill.eventscan.services.handlers;

import io.lastwill.eventscan.events.ContractCreatedEvent;
import io.lastwill.eventscan.messages.ContractDeployedNotify;
import io.lastwill.eventscan.messages.PaymentStatus;
import io.lastwill.eventscan.services.ExternalNotifier;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.context.PayloadApplicationEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class CreateContractHandler implements ApplicationListener<PayloadApplicationEvent> {
    @Autowired
    private ExternalNotifier externalNotifier;

    @Override
    public void onApplicationEvent(PayloadApplicationEvent springEvent) {
        Object event = springEvent.getPayload();
        if (event instanceof ContractCreatedEvent) handleContractCreateTransaction((ContractCreatedEvent) event);
    }

    private void handleContractCreateTransaction(ContractCreatedEvent event) {
        externalNotifier.send(
                event.getNetworkType(),
                new ContractDeployedNotify(
                        event.getContract().getId(),
                        PaymentStatus.COMMITTED,
                        event.getTransaction().getCreates(),
                        event.getTransaction().getHash(),
                        event.isSuccess()
                )
        );
    }
}
