package io.lastwill.eventscan.events.model.contract;


import io.mywish.blockchain.ContractEvent;
import io.mywish.blockchain.ContractEventDefinition;

public class BaseEmptyEvent extends ContractEvent {
    public BaseEmptyEvent(ContractEventDefinition definition, String address) {
        super(definition, address);
    }
}