package io.lastwill.eventscan.events.model.contract;

import io.mywish.blockchain.ContractEventDefinition;

public class NotifiedEvent extends BaseEmptyEvent {
    public NotifiedEvent(ContractEventDefinition definition, String address) {
        super(definition, address);
    }
}
