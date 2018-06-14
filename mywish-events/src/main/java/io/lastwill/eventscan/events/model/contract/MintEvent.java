package io.lastwill.eventscan.events.model.contract;

import io.mywish.wrapper.ContractEvent;
import io.mywish.wrapper.WrapperTransactionReceipt;
import io.mywish.wrapper.ContractEventDefinition;
import lombok.Getter;

import java.math.BigInteger;

@Getter
public class MintEvent extends ContractEvent {
    private final String to;
    private final BigInteger amount;

    public MintEvent(ContractEventDefinition definition, WrapperTransactionReceipt transactionReceipt, String to, BigInteger amount, String address) {
        super(definition, address, transactionReceipt);
        this.to = to;
        this.amount = amount;
    }
}
