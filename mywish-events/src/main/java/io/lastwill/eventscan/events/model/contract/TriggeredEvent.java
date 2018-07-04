package io.lastwill.eventscan.events.model.contract;

import io.mywish.wrapper.ContractEvent;
import io.mywish.wrapper.ContractEventDefinition;
import io.mywish.wrapper.WrapperTransactionReceipt;
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
