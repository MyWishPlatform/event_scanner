package io.lastwill.eventscan.events.model.contract.eos;

import io.mywish.blockchain.ContractEvent;
import io.mywish.blockchain.ContractEventDefinition;

public class SetCodeEvent extends ContractEvent {

    public SetCodeEvent(ContractEventDefinition definition, String address) {
        super(definition, address);
    }
}
