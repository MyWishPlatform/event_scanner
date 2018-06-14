package io.lastwill.eventscan.events.model.contract;

import io.mywish.wrapper.ContractEvent;
import io.mywish.wrapper.ContractEventDefinition;
import io.mywish.wrapper.WrapperTransactionReceipt;
import lombok.Getter;

@Getter
public class OwnershipTransferredEvent extends ContractEvent {
    private final String previousOwner;
    private final String newOwner;

    public OwnershipTransferredEvent(final ContractEventDefinition definition, final WrapperTransactionReceipt transactionReceipt, String previousOwner, String newOwner, String address) {
        super(definition, address, transactionReceipt);
        this.previousOwner = previousOwner;
        this.newOwner = newOwner;
    }
}
