package io.lastwill.eventscan.events.model;

import io.mywish.wrapper.ContractEvent;
import io.mywish.wrapper.ContractEventDefinition;
import lombok.Getter;

@Getter
public class CreateAccountEvent extends ContractEvent {
    private final String creator;
    private final String name;

    public CreateAccountEvent(ContractEventDefinition definition, String address, String creator, String name) {
        super(definition, address);
        this.creator = creator;
        this.name = name;
    }
}
