package io.lastwill.eventscan.events.contract;

import io.mywish.scanner.WrapperTransactionReceipt;
import lombok.Getter;

@Getter
public class ContractEvent {
    protected final ContractEventDefinition definition;
    protected final String address;
    protected final WrapperTransactionReceipt transactionReceipt;

    public ContractEvent(final ContractEventDefinition definition, final String address, final WrapperTransactionReceipt transactionReceipt) {
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
