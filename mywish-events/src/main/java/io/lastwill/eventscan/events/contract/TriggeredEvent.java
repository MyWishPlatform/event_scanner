package io.lastwill.eventscan.events.contract;

import io.mywish.wrapper.ContractEvent;
import io.mywish.wrapper.ContractEventDefinition;
import io.mywish.wrapper.WrapperTransactionReceipt;
import lombok.Getter;
import java.math.BigInteger;

@Getter
public class TriggeredEvent extends ContractEvent {
    private final BigInteger balance;
    public TriggeredEvent(ContractEventDefinition definition, WrapperTransactionReceipt transactionReceipt, BigInteger balance, String address) {
        super(definition, address, transactionReceipt);
        this.balance = balance;
    }
}
