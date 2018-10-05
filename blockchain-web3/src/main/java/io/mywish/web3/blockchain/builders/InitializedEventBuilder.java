package io.mywish.web3.blockchain.builders;

import io.lastwill.eventscan.events.model.contract.InitializedEvent;
import io.mywish.blockchain.ContractEventDefinition;
import org.springframework.stereotype.Component;

@Component
public class InitializedEventBuilder extends BaseEmptyEventBuilder<InitializedEvent> {
    public InitializedEventBuilder() {
        super("Initialized");
    }

    @Override
    protected InitializedEvent buildEmpty(final ContractEventDefinition definition, String address) {
        return new InitializedEvent(definition, address);
    }
}
