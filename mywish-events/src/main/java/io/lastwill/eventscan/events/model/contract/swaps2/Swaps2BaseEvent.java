package io.lastwill.eventscan.events.model.contract.swaps2;

import io.mywish.blockchain.ContractEvent;
import io.mywish.blockchain.ContractEventDefinition;
import lombok.Getter;

@Getter
public abstract class Swaps2BaseEvent extends ContractEvent {
    private final String id;

    public Swaps2BaseEvent(ContractEventDefinition definition, String id, String address) {
        super(definition, address);
        this.id = id;
    }
}
