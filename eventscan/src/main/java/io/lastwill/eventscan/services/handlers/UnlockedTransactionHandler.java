package io.lastwill.eventscan.services.handlers;

import io.lastwill.eventscan.events.TransactionUnlockedEvent;
import io.lastwill.eventscan.helpers.TransactionHelper;
import io.lastwill.eventscan.services.ExternalNotifier;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@ConditionalOnBean(ExternalNotifier.class)
public class UnlockedTransactionHandler {
    @Autowired
    private ExternalNotifier externalNotifier;

    @EventListener
    public void handleTransactionUnlockedEvent(TransactionUnlockedEvent event) {
        externalNotifier.sendTransactionCompletedNotification(
                event.getTransaction().getHash(),
                TransactionHelper.isSuccess(event.getTransactionReceipt()),
                event.getAddressLock()
        );
    }
}
