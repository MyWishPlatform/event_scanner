package io.mywish.tron.blockchain.builders;

import io.lastwill.eventscan.events.model.contract.NotifiedEvent;
import io.mywish.blockchain.ContractEventDefinition;
import io.mywish.troncli4j.model.EventResult;
import org.springframework.stereotype.Component;

@Component
public class NotifiedTronEventBuilder extends TronEventBuilder<NotifiedEvent> {
    private static final ContractEventDefinition DEFINITION = new ContractEventDefinition("Notified");

    @Override
    public NotifiedEvent build(String address, EventResult event) {
        return new NotifiedEvent(DEFINITION, address);
    }

    @Override
    public ContractEventDefinition getDefinition() {
        return DEFINITION;
    }
}
