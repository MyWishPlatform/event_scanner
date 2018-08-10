package io.lastwill.eventscan.events.model.contract;

import io.mywish.wrapper.ContractEvent;
import io.mywish.wrapper.ContractEventDefinition;
import lombok.Getter;

@Getter
public class CreateAccountEvent extends ContractEvent {
    private final String creator;
    private final String created;

    public CreateAccountEvent(ContractEventDefinition definition, String address, String creator, String created) {
        super(definition, address);
        this.creator = creator;
        this.created = created;
    }
}
