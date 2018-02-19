package io.lastwill.eventscan.events.contract;

import lombok.Getter;
import org.web3j.protocol.core.methods.response.TransactionReceipt;

import java.math.BigInteger;

@Getter
public class FundsAddedEvent extends ContractEvent {
    private final String from;
    private final BigInteger amount;

    public FundsAddedEvent(ContractEventDefinition definition, TransactionReceipt transactionReceipt, String from, BigInteger amount, String address) {
        super(definition, address, transactionReceipt);
        this.from = from;
        this.amount = amount;
    }
}
