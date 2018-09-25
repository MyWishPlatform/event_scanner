package io.lastwill.eventscan.events.model.contract.investmentPool;

import io.mywish.blockchain.ContractEvent;
import io.mywish.blockchain.ContractEventDefinition;
import lombok.Getter;

import java.math.BigInteger;

/**
 * Occurs when user invest ETH to a pool contract.
 */
@Getter
public class InvestEvent extends ContractEvent {
    private final String investorAddress;
    private final BigInteger amount;
    public InvestEvent(ContractEventDefinition definition, String address, String investorAddress, BigInteger amount) {
        super(definition, address);
        this.investorAddress = investorAddress;
        this.amount = amount;
    }
}
