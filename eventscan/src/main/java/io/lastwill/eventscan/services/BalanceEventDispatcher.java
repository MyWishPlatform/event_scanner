package io.lastwill.eventscan.services;

import io.lastwill.eventscan.events.OwnerBalanceChangedEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class BalanceEventDispatcher {
    @Autowired
    private ExternalNotifier externalNotifier;

    @Autowired
    private CommitmentService commitmentService;

    @EventListener
    public void ownerBalanceChangedHandler(final OwnerBalanceChangedEvent event) {
        try {
            externalNotifier.sendPaymentNotify(event.getContract(), event.getBalance(), ExternalNotifier.PaymentStatus.CREATED);

            commitmentService.waitCommitment(event.getBlock().getHash(), event.getBlock().getNumber().longValue())
                    .thenApply(committed -> committed ? ExternalNotifier.PaymentStatus.COMMITTED : ExternalNotifier.PaymentStatus.REJECTED)
                    .thenAccept(status -> externalNotifier.sendPaymentNotify(event.getContract(), event.getBalance(), status))
                    .exceptionally(th -> {
                        log.error("Waiting commitment failed.", th);
                        return null;
                    });
        }
        catch (Throwable e) {
            log.error("Sending notification about new balance failed.", e);
        }
    }
}
