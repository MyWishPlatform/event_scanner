package io.lastwill.eventscan.services;

import io.lastwill.eventscan.events.OwnerBalanceChangedEvent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class BalanceEventDispatcher {
    @Autowired
    private ExternalNotifier externalNotifier;

    public void ownerBalanceChangedHandler(OwnerBalanceChangedEvent event) {

    }
}
