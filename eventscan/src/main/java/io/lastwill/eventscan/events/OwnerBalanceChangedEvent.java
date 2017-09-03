package io.lastwill.eventscan.events;

import java.math.BigInteger;

public class OwnerBalanceChangedEvent extends BalanceChangedEvent {
    public OwnerBalanceChangedEvent(String address, BigInteger amount, BigInteger balance) {
        super(address, amount, balance);
    }
}
