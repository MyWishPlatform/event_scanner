package io.lastwill.eventscan.events.model;

import io.mywish.wrapper.ContractEvent;
import io.mywish.wrapper.ContractEventDefinition;

public class SetCodeEvent extends ContractEvent {

    public SetCodeEvent(ContractEventDefinition definition, String address) {
        super(definition, address);
    }
}
