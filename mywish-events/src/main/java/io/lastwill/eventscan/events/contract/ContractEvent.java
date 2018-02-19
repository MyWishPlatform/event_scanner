package io.lastwill.eventscan.events.contract;

import lombok.Getter;
import org.web3j.protocol.core.methods.response.TransactionReceipt;

@Getter
public class ContractEvent {
    protected final ContractEventDefinition definition;
    protected final String address;
    protected final TransactionReceipt transactionReceipt;

    public ContractEvent(final ContractEventDefinition definition, final String address, final TransactionReceipt transactionReceipt) {
        this.definition = definition;
        this.address = address;
        this.transactionReceipt = transactionReceipt;
    }

    public String getName() {
        return definition.getEvent().getName();
    }

    public String getSignature() {
        return definition.getSignature();
    }
}
