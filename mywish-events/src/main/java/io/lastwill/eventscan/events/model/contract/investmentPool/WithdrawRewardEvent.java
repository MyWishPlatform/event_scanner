package io.lastwill.eventscan.events.model.contract.investmentPool;

import io.mywish.blockchain.ContractEvent;
import io.mywish.blockchain.ContractEventDefinition;
import lombok.Getter;

import java.math.BigInteger;

@Getter
public class WithdrawRewardEvent extends ContractEvent {
    private final String adminAddress;
    private final BigInteger amount;

    public WithdrawRewardEvent(ContractEventDefinition definition, String address, String adminAddress, BigInteger amount) {
        super(definition, address);
        this.adminAddress = adminAddress;
        this.amount = amount;
    }
}
