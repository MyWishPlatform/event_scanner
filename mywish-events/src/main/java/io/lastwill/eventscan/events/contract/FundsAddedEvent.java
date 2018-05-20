package io.lastwill.eventscan.events.contract;

import io.mywish.wrapper.ContractEvent;
import io.mywish.wrapper.ContractEventDefinition;
import io.mywish.wrapper.WrapperTransactionReceipt;
import lombok.Getter;
import java.math.BigInteger;

@Getter
public class FundsAddedEvent extends ContractEvent {
    private final String from;
    private final BigInteger amount;

    public FundsAddedEvent(ContractEventDefinition definition, WrapperTransactionReceipt transactionReceipt, String from, BigInteger amount, String address) {
        super(definition, address, transactionReceipt);
        this.from = from;
        this.amount = amount;
    }
}
