package io.lastwill.eventscan.events;

import java.math.BigInteger;

public class ContractBalanceChangedEvent extends BalanceChangedEvent {
    public ContractBalanceChangedEvent(String address, BigInteger amount, BigInteger balance) {
        super(address, amount, balance);
    }
}
