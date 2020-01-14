package io.lastwill.eventscan.events.model.contract.tokenProtector;

import io.mywish.blockchain.ContractEvent;
import io.mywish.blockchain.ContractEventDefinition;
import lombok.Getter;

@Getter
public class SelfdestructionEvent extends ContractEvent {
    private final boolean status;

    public SelfdestructionEvent(ContractEventDefinition definition, String address, boolean status) {
        super(definition, address);
        this.status = status;
    }
}
