package io.lastwill.eventscan.events.model.contract.erc20;

import io.mywish.blockchain.ContractEvent;
import io.mywish.blockchain.ContractEventDefinition;
import lombok.Getter;

import java.math.BigInteger;

@Getter
public class ApprovalEvent extends ContractEvent {
    private final String owner;
    private final String spender;
    private final BigInteger tokens;

    public ApprovalEvent(ContractEventDefinition definition, String owner, String spender, BigInteger tokens, String address) {
        super(definition, address);
        this.owner = owner;
        this.spender = spender;
        this.tokens = tokens;
    }
}