package io.lastwill.eventscan.events.model.contract.swaps;

import io.mywish.blockchain.ContractEvent;
import io.mywish.blockchain.ContractEventDefinition;
import lombok.Getter;

@Getter
public class SwapEvent extends ContractEvent {
    private final String byUser;

    public SwapEvent(ContractEventDefinition definition, String byUser, String address) {
        super(definition, address);
        this.byUser = byUser;
    }
}
