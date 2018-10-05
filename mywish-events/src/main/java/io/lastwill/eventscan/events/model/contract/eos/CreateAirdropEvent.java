package io.lastwill.eventscan.events.model.contract.eos;

import io.mywish.blockchain.ContractEvent;
import io.mywish.blockchain.ContractEventDefinition;
import lombok.Getter;

@Getter
public class CreateAirdropEvent extends ContractEvent {
    private final long pk;
    private final String issuer;
    private final String symbol;
    private final String tokenAddress;
    private final int decimals;
    private final long dropsCount;

    public CreateAirdropEvent(final ContractEventDefinition definition, String address, long pk, String issuer, String symbol, String tokenAddress, int decimals, long dropsCount) {
        super(definition, address);
        this.pk = pk;
        this.issuer = issuer;
        this.symbol = symbol;
        this.tokenAddress = tokenAddress;
        this.decimals = decimals;
        this.dropsCount = dropsCount;
    }
}
