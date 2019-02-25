package io.mywish.tron.blockchain.builders;

import io.lastwill.eventscan.events.model.contract.KilledEvent;
import io.mywish.blockchain.ContractEventDefinition;
import io.mywish.troncli4j.model.EventResult;
import org.springframework.stereotype.Component;

@Component
public class KilledTronEventBuilder extends TronEventBuilder<KilledEvent> {
    private static final ContractEventDefinition DEFINITION = new ContractEventDefinition("Killed");

    @Override
    public KilledEvent build(String address, EventResult event) {
        return new KilledEvent(
                DEFINITION,
                Boolean.valueOf(event.getResult().get("byUser")),
                address
        );
    }

    @Override
    public ContractEventDefinition getDefinition() {
        return DEFINITION;
    }
}
