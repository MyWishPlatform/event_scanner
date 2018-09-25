package io.lastwill.eventscan.events.model.contract;

import io.mywish.blockchain.ContractEvent;
import io.mywish.blockchain.ContractEventDefinition;
import lombok.Getter;
import java.math.BigInteger;

@Getter
public class FundsAddedEvent extends ContractEvent {
    private final String from;
    private final BigInteger amount;

    public FundsAddedEvent(ContractEventDefinition definition, String from, BigInteger amount, String address) {
        super(definition, address);
        this.from = from;
        this.amount = amount;
    }
}
