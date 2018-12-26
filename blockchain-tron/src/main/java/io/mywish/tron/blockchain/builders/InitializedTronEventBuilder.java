package io.mywish.tron.blockchain.builders;

import io.lastwill.eventscan.events.model.contract.InitializedEvent;
import io.mywish.blockchain.ContractEventDefinition;
import io.mywish.troncli4j.model.EventResult;
import org.springframework.stereotype.Component;

@Component
public class InitializedTronEventBuilder extends TronEventBuilder<InitializedEvent> {
    private final static ContractEventDefinition DEFINITION = new ContractEventDefinition("Initialized");

    @Override
    public InitializedEvent build(String address, EventResult event) {
        return new InitializedEvent(
                DEFINITION,
                address
        );
    }

    @Override
    public ContractEventDefinition getDefinition() {
        return DEFINITION;
    }
}