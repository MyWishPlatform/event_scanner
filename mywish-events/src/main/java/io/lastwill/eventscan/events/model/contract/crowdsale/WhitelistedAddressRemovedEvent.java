package io.lastwill.eventscan.events.model.contract.crowdsale;

import io.mywish.wrapper.ContractEventDefinition;
import io.mywish.wrapper.WrapperTransactionReceipt;
import lombok.Getter;

@Getter
public class WhitelistedAddressRemovedEvent extends WhitelistedEvent {
    public WhitelistedAddressRemovedEvent(ContractEventDefinition definition, WrapperTransactionReceipt transactionReceipt, String address, String addedAddress) {
        super(definition, transactionReceipt, address, addedAddress);
    }
}
