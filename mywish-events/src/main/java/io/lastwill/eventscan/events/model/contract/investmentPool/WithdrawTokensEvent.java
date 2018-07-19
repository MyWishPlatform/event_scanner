package io.lastwill.eventscan.events.model.contract.investmentPool;

import io.mywish.wrapper.ContractEvent;
import io.mywish.wrapper.ContractEventDefinition;
import lombok.Getter;

import java.math.BigInteger;

/**
 * Occurs when investor get tokens from a pool contract.
 */
@Getter
public class WithdrawTokensEvent extends ContractEvent {
    private final String investorAddress;
    private final BigInteger amount;
    public WithdrawTokensEvent(ContractEventDefinition definition, String address, String investorAddress, BigInteger amount) {
        super(definition, address);
        this.investorAddress = investorAddress;
        this.amount = amount;
    }
}
