package io.lastwill.eventscan.services;

import io.lastwill.eventscan.events.OwnerBalanceChangedEvent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
public class BalanceEventDispatcher {
    @Autowired
    private ExternalNotifier externalNotifier;

    @Autowired
    private CommitmentService commitmentService;

    @EventListener
    public void ownerBalanceChangedHandler(final OwnerBalanceChangedEvent event) {
        externalNotifier.sendNotify(event.getContract(), event.getBalance(), ExternalNotifier.PaymentStatus.CREATED);

        commitmentService.register(event.getBlock().getHash(), event.getBlock().getNumber().longValue())
                .thenApply(committed -> committed ? ExternalNotifier.PaymentStatus.COMMITTED : ExternalNotifier.PaymentStatus.REJECTED)
                .thenAccept(status -> externalNotifier.sendNotify(event.getContract(), event.getBalance(), status));
    }


}
