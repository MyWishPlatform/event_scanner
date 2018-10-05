package io.lastwill.eventscan.events.model.contract;

import io.mywish.blockchain.ContractEventDefinition;

public class InitializedEvent extends BaseEmptyEvent {
    public InitializedEvent(ContractEventDefinition definition, String address) {
        super(definition, address);
    }
}
