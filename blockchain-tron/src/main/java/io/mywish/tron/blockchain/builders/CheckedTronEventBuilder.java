package io.mywish.tron.blockchain.builders;

import io.lastwill.eventscan.events.model.contract.CheckedEvent;
import io.mywish.blockchain.ContractEventDefinition;
import io.mywish.troncli4j.model.EventResult;
import org.springframework.stereotype.Component;

@Component
public class CheckedTronEventBuilder extends TronEventBuilder<CheckedEvent> {
    private final static ContractEventDefinition DEFINITION = new ContractEventDefinition("Checked");

    @Override
    public CheckedEvent build(String address, EventResult event) {
        return new CheckedEvent(
                DEFINITION,
                Boolean.valueOf(event.getResult().get("isAccident")),
                address
        );
    }

    @Override
    public ContractEventDefinition getDefinition() {
        return DEFINITION;
    }
}
