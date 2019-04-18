package io.lastwill.eventscan.events.model.contract.swaps2;

import io.mywish.blockchain.ContractEvent;
import io.mywish.blockchain.ContractEventDefinition;
import lombok.Getter;

@Getter
public class CancelEvent extends Swaps2BaseEvent {
    public CancelEvent(ContractEventDefinition definition, String id, String address) {
        super(definition, id, address);
    }
}
