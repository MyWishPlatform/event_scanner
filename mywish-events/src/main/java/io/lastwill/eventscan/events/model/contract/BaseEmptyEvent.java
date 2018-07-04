package io.lastwill.eventscan.events.model.contract;


import io.mywish.wrapper.ContractEvent;
import io.mywish.wrapper.ContractEventDefinition;
import io.mywish.wrapper.WrapperTransactionReceipt;

public class BaseEmptyEvent extends ContractEvent {
    public BaseEmptyEvent(ContractEventDefinition definition, String address) {
        super(definition, address);
    }
}