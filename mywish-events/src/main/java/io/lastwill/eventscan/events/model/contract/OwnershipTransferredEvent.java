package io.lastwill.eventscan.events.model.contract;

import io.mywish.blockchain.ContractEvent;
import io.mywish.blockchain.ContractEventDefinition;
import lombok.Getter;

@Getter
public class OwnershipTransferredEvent extends ContractEvent {
    private final String previousOwner;
    private final String newOwner;

    public OwnershipTransferredEvent(final ContractEventDefinition definition, String previousOwner, String newOwner, String address) {
        super(definition, address);
        this.previousOwner = previousOwner;
        this.newOwner = newOwner;
    }
}
