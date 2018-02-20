package io.lastwill.eventscan.events.contract;

import lombok.Getter;
import org.web3j.protocol.core.methods.response.TransactionReceipt;

import java.math.BigInteger;

@Getter
public class TriggeredEvent extends ContractEvent {
    private final BigInteger balance;
    public TriggeredEvent(ContractEventDefinition definition, TransactionReceipt transactionReceipt, BigInteger balance, String address) {
        super(definition, address, transactionReceipt);
        this.balance = balance;
    }
}
