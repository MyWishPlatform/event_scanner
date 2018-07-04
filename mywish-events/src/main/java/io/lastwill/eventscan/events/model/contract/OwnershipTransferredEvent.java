package io.lastwill.eventscan.events.model.contract;

import io.mywish.wrapper.ContractEvent;
import io.mywish.wrapper.ContractEventDefinition;
import io.mywish.wrapper.WrapperTransactionReceipt;
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
