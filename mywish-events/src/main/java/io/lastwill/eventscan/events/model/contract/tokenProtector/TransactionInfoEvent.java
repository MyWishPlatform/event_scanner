package io.lastwill.eventscan.events.model.contract.tokenProtector;

import io.mywish.blockchain.ContractEvent;
import io.mywish.blockchain.ContractEventDefinition;
import lombok.Getter;

import java.math.BigInteger;

@Getter
public class TransactionInfoEvent extends ContractEvent {
    private final String tokenContract;
    private final BigInteger amount;

    public TransactionInfoEvent(ContractEventDefinition definition, String address,String tokenContract, BigInteger amount) {
        super(definition, address);
        this.tokenContract = tokenContract;
        this.amount = amount;

    }
}
