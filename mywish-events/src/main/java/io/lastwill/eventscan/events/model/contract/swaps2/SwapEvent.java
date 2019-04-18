package io.lastwill.eventscan.events.model.contract.swaps2;

import io.mywish.blockchain.ContractEvent;
import io.mywish.blockchain.ContractEventDefinition;
import lombok.Getter;

@Getter
public class SwapEvent extends Swaps2BaseEvent {
    private final String byUser;

    public SwapEvent(ContractEventDefinition definition, String id, String byUser, String address) {
        super(definition, id, address);
        this.byUser = byUser;
    }
}
