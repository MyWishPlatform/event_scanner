package io.lastwill.eventscan.events.model.contract.crowdsale;

import io.mywish.wrapper.ContractEvent;
import io.mywish.wrapper.ContractEventDefinition;
import io.mywish.wrapper.WrapperTransactionReceipt;
import lombok.Getter;

@Getter
public class WhitelistedAddressAddedEvent extends WhitelistedEvent {
    public WhitelistedAddressAddedEvent(ContractEventDefinition definition, WrapperTransactionReceipt transactionReceipt, String address, String addedAddress) {
        super(definition, transactionReceipt, address, addedAddress);
    }
}
