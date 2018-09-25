package io.lastwill.eventscan.events.model.contract;

import io.mywish.blockchain.ContractEventDefinition;

public class MintFinishedEvent extends BaseEmptyEvent {
    public MintFinishedEvent(ContractEventDefinition definition, String address) {
        super(definition, address);
    }
}
