package io.lastwill.eventscan.events.model.contract.swaps;

import io.lastwill.eventscan.events.model.contract.BaseEmptyEvent;
import io.mywish.blockchain.ContractEventDefinition;

public class CancelEvent extends BaseEmptyEvent {
    public CancelEvent(ContractEventDefinition definition, String address) {
        super(definition, address);
    }
}
