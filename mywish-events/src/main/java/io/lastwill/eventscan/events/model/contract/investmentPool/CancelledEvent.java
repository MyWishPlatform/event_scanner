package io.lastwill.eventscan.events.model.contract.investmentPool;

import io.lastwill.eventscan.events.model.contract.BaseEmptyEvent;
import io.mywish.blockchain.ContractEventDefinition;

public class CancelledEvent extends BaseEmptyEvent {
    public CancelledEvent(ContractEventDefinition definition, String address) {
        super(definition, address);
    }
}
