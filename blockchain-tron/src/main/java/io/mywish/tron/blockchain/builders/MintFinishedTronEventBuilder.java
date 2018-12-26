package io.mywish.tron.blockchain.builders;

import io.lastwill.eventscan.events.model.contract.MintFinishedEvent;
import io.mywish.blockchain.ContractEventDefinition;
import io.mywish.troncli4j.model.EventResult;
import org.springframework.stereotype.Component;

@Component
public class MintFinishedTronEventBuilder extends TronEventBuilder<MintFinishedEvent> {
    private final static ContractEventDefinition DEFINITION = new ContractEventDefinition("MintFinished");

    @Override
    public MintFinishedEvent build(String address, EventResult event) {
        return new MintFinishedEvent(
                DEFINITION,
                address
        );
    }

    @Override
    public ContractEventDefinition getDefinition() {
        return DEFINITION;
    }
}