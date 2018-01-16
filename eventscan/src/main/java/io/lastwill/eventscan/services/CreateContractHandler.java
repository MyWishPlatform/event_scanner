package io.lastwill.eventscan.services;

import io.lastwill.eventscan.events.ContractCreatedEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@ConditionalOnBean(ExternalNotifier.class)
public class CreateContractHandler {
    @Autowired
    private ExternalNotifier externalNotifier;

    @EventListener
    public void handleContractCreateTransaction(ContractCreatedEvent event) {
        if (externalNotifier == null) {
            return;
        }
        externalNotifier.sendDeployedNotification(
                event.getContract(),
                event.getTransaction().getCreates(),
                event.getTransaction().getHash(),
                true,
                event.isSuccess()
        );
    }
}
