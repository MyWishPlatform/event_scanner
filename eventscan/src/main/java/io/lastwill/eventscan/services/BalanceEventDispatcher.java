package io.lastwill.eventscan.services;

import io.lastwill.eventscan.events.UserPaymentEvent;
import io.lastwill.eventscan.messages.PaymentNotify;
import io.lastwill.eventscan.messages.PaymentStatus;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@ConditionalOnBean(ExternalNotifier.class)
public class BalanceEventDispatcher {
    @Autowired
    private ExternalNotifier externalNotifier;

    @EventListener
    private void ownerBalanceChangedHandler(final UserPaymentEvent event) {
        try {
            externalNotifier.send(
                    event.getNetworkType(),
                    new PaymentNotify(
                            event.getUserProfile().getUser().getId(),
                            event.getAmount(),
                            PaymentStatus.COMMITTED,
                            event.getTransaction().getHash(),
                            event.getCurrency(),
                            event.isSuccess()
                    )
            );
        }
        catch (Throwable e) {
            log.error("Sending notification about new balance failed.", e);
        }
    }
}
