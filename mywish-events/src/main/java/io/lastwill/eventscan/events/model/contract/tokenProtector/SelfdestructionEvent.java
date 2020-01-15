package io.lastwill.eventscan.events.model.contract.tokenProtector;

import io.mywish.blockchain.ContractEvent;
import io.mywish.blockchain.ContractEventDefinition;
import lombok.Getter;

@Getter
public class SelfdestructionEvent extends ContractEvent {
    private final boolean eventStatus;

    public SelfdestructionEvent(ContractEventDefinition definition, String address, boolean eventStatus) {
        super(definition, address);
        this.eventStatus = eventStatus;
    }
}
