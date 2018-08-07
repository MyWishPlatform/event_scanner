package io.lastwill.eventscan.events.model;

import io.mywish.wrapper.ContractEvent;
import io.mywish.wrapper.ContractEventDefinition;
import lombok.Getter;

@Getter
public class CreateTokenEvent extends ContractEvent {
    private String issuer;
    private String supply;

    public CreateTokenEvent(final ContractEventDefinition definition, String address, String issuer, String supply) {
        super(definition, address);
        this.issuer = issuer;
        this.supply = supply;
    }
}
