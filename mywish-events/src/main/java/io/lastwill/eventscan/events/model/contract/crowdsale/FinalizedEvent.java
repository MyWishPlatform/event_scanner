package io.lastwill.eventscan.events.model.contract.crowdsale;


import io.lastwill.eventscan.events.model.contract.BaseEmptyEvent;
import io.mywish.blockchain.ContractEventDefinition;

public class FinalizedEvent extends BaseEmptyEvent {
    public FinalizedEvent(ContractEventDefinition definition, String address) {
        super(definition, address);
    }
}
