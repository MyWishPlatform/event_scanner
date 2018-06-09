package io.lastwill.eventscan.services.handlers;

import io.lastwill.eventscan.events.model.ContractCreatedEvent;
import io.lastwill.eventscan.messages.ContractDeployedNotify;
import io.lastwill.eventscan.messages.PaymentStatus;
import io.lastwill.eventscan.services.ExternalNotifier;
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
