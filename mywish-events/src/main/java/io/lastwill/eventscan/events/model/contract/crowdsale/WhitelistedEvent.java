package io.lastwill.eventscan.events.model.contract.crowdsale;

import io.mywish.blockchain.ContractEvent;
import io.mywish.blockchain.ContractEventDefinition;
import lombok.Getter;

@Getter
public abstract class WhitelistedEvent extends ContractEvent {
    private final String whitelistedAddress;

    public WhitelistedEvent(ContractEventDefinition definition, String address, String whitelistedAddress) {
        super(definition, address);
        this.whitelistedAddress = whitelistedAddress;
    }
}
