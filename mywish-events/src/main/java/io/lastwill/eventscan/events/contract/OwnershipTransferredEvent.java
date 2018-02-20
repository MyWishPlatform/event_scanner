package io.lastwill.eventscan.events.contract;

import lombok.Getter;
import org.web3j.protocol.core.methods.response.TransactionReceipt;

@Getter
public class OwnershipTransferredEvent extends ContractEvent {
    private final String previousOwner;
    private final String newOwner;

    public OwnershipTransferredEvent(final ContractEventDefinition definition, final TransactionReceipt transactionReceipt, String previousOwner, String newOwner, String address) {
        super(definition, address, transactionReceipt);
        this.previousOwner = previousOwner;
        this.newOwner = newOwner;
    }
}
