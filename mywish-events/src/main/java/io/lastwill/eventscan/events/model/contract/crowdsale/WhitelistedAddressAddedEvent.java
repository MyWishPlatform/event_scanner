package io.lastwill.eventscan.events.model.contract.crowdsale;

import io.mywish.wrapper.ContractEventDefinition;
import io.mywish.wrapper.WrapperTransactionReceipt;
import lombok.Getter;

@Getter
public class WhitelistedAddressAddedEvent extends WhitelistedEvent {
    public WhitelistedAddressAddedEvent(ContractEventDefinition definition, String address, String addedAddress) {
        super(definition, address, addedAddress);
    }
}
