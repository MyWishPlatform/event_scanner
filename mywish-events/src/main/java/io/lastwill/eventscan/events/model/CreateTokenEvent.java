package io.lastwill.eventscan.events.model;

import io.mywish.blockchain.ContractEvent;
import io.mywish.blockchain.ContractEventDefinition;
import lombok.Getter;

import java.math.BigDecimal;

@Getter
public class CreateTokenEvent extends ContractEvent {
    private final String issuer;
    private final String symbol;
    private final BigDecimal maxSupply;
    private final int decimals;

    public CreateTokenEvent(final ContractEventDefinition definition, String address, String issuer, String symbol, BigDecimal maxSupply, int decimals) {
        super(definition, address);
        this.issuer = issuer;
        this.symbol = symbol;
        this.maxSupply = maxSupply;
        this.decimals = decimals;
    }
}
