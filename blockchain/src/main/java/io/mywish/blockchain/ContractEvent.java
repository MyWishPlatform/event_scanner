package io.mywish.blockchain;

import lombok.Getter;

@Getter
public class ContractEvent {
    protected final ContractEventDefinition definition;
    protected final String address;

    public ContractEvent(final ContractEventDefinition definition, final String address) {
        this.definition = definition;
        this.address = address;
    }

    public String getEventName() {
        return definition.getName();
    }
}
