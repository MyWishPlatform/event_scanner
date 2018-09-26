package io.lastwill.eventscan.events.model;

import io.mywish.blockchain.ContractEvent;
import io.mywish.blockchain.ContractEventDefinition;
import lombok.Getter;

import java.math.BigDecimal;

@Getter
public class CreateAirdropEvent extends ContractEvent {
    private final String issuer;
    private final String symbol;
    private final String tokenAddress;
    private final int decimals;

    public CreateAirdropEvent(final ContractEventDefinition definition, String address, String issuer, String symbol, String tokenAddress, int decimals) {
        super(definition, address);
        this.issuer = issuer;
        this.symbol = symbol;
        this.tokenAddress = tokenAddress;
        this.decimals = decimals;
    }
}
