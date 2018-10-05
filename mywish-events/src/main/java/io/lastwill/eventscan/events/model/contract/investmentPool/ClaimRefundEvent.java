package io.lastwill.eventscan.events.model.contract.investmentPool;

import io.mywish.blockchain.ContractEvent;
import io.mywish.blockchain.ContractEventDefinition;
import lombok.Getter;

import java.math.BigInteger;

@Getter
public class ClaimRefundEvent extends ContractEvent {
    private final BigInteger amount;
    public ClaimRefundEvent(ContractEventDefinition definition, String address, BigInteger amount) {
        super(definition, address);
        this.amount = amount;
    }
}
