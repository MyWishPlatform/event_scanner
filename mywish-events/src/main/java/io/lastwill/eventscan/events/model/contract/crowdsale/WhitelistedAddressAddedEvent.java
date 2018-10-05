package io.lastwill.eventscan.events.model.contract.crowdsale;

import io.mywish.blockchain.ContractEventDefinition;
import lombok.Getter;

@Getter
public class WhitelistedAddressAddedEvent extends WhitelistedEvent {
    public WhitelistedAddressAddedEvent(ContractEventDefinition definition, String address, String addedAddress) {
        super(definition, address, addedAddress);
    }
}
