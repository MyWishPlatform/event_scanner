package io.lastwill.eventscan.events.model.contract;

import io.mywish.blockchain.ContractEvent;
import io.mywish.blockchain.ContractEventDefinition;
import lombok.Getter;

import java.math.BigInteger;

@Getter
public class MintEvent extends ContractEvent {
    private final String to;
    private final BigInteger amount;

    public MintEvent(ContractEventDefinition definition, String to, BigInteger amount, String address) {
        super(definition, address);
        this.to = to;
        this.amount = amount;
    }
}
