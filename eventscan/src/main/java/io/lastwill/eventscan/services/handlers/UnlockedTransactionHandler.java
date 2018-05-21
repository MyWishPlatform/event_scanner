package io.lastwill.eventscan.services.handlers;

import io.lastwill.eventscan.events.TransactionUnlockedEvent;
import io.lastwill.eventscan.messages.TransactionCompletedNotify;
import io.lastwill.eventscan.services.ExternalNotifier;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.context.event.EventListener;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@ConditionalOnBean(ExternalNotifier.class)
public class UnlockedTransactionHandler {
    @Autowired
    private ExternalNotifier externalNotifier;

    // TransactionCompletedNotify must be the first
    @Order(Ordered.LOWEST_PRECEDENCE - 1)
    @EventListener
    private void handleTransactionUnlockedEvent(TransactionUnlockedEvent event) {
        externalNotifier.send(event.getNetworkType(),
                new TransactionCompletedNotify(
                        event.getTransaction().getHash(),
                        event.getAddressLock().getId(),
                        event.getAddressLock().getAddress(),
                        event.getAddressLock().getLockedBy(),
                        event.getTransactionReceipt().isSuccess()
                )
        );
    }
}
