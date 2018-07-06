package io.mywish.wrapper;

import lombok.Getter;

@Getter
public class ContractEvent {
    protected final ContractEventDefinition definition;
    protected final String address;

    public ContractEvent(final ContractEventDefinition definition, final String address) {
        this.definition = definition;
        this.address = address;
    }

    public String getName() {
        return definition.getEvent().getName();
    }

    public String getSignature() {
        return definition.getSignature();
    }
}
