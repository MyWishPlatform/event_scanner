package io.lastwill.eventscan.services;

import io.lastwill.eventscan.events.ContractCreatedEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class CreateContractHandler {
    @Autowired
    private ExternalNotifier externalNotifier;

    @EventListener
    public void handleContractCreateTransaction(ContractCreatedEvent event) {
        externalNotifier.sendDeployedNotification(
                event.getContract(),
                event.getTransaction().getCreates(),
                event.getTransaction().getHash(),
                true
        );
    }
}
