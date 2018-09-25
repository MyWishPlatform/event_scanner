package io.lastwill.eventscan.events.model.contract;

import io.mywish.blockchain.ContractEvent;
import io.mywish.blockchain.ContractEventDefinition;
import lombok.Getter;
import java.math.BigInteger;

@Getter
public class TriggeredEvent extends ContractEvent {
    private final BigInteger balance;
    public TriggeredEvent(ContractEventDefinition definition, BigInteger balance, String address) {
        super(definition, address);
        this.balance = balance;
    }
}
