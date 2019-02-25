package io.mywish.tron.blockchain.builders;

import io.lastwill.eventscan.events.model.contract.TriggeredEvent;
import io.mywish.blockchain.ContractEventDefinition;
import io.mywish.troncli4j.model.EventResult;
import org.springframework.stereotype.Component;

import java.math.BigInteger;

@Component
public class TriggeredTronEventBuilder extends TronEventBuilder<TriggeredEvent> {
    private static final ContractEventDefinition DEFINITION = new ContractEventDefinition("Triggered");

    @Override
    public TriggeredEvent build(String address, EventResult event) {
        return new TriggeredEvent(
                DEFINITION,
                new BigInteger(event.getResult().get("balance")),
                address
        );
    }

    @Override
    public ContractEventDefinition getDefinition() {
        return DEFINITION;
    }
}
