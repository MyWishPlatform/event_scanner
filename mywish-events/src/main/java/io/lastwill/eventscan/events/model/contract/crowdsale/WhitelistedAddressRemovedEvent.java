package io.lastwill.eventscan.events.model.contract.crowdsale;

import io.mywish.blockchain.ContractEventDefinition;
import lombok.Getter;

@Getter
public class WhitelistedAddressRemovedEvent extends WhitelistedEvent {
    public WhitelistedAddressRemovedEvent(ContractEventDefinition definition, String address, String addedAddress) {
        super(definition, address, addedAddress);
    }
}
