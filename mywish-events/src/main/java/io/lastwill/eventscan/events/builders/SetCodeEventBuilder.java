package io.lastwill.eventscan.events.builders;

import io.lastwill.eventscan.events.model.SetCodeEvent;
import io.mywish.wrapper.ContractEventBuilder;
import io.mywish.wrapper.ContractEventDefinition;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;

@Component
public class SetCodeEventBuilder extends ContractEventBuilder<SetCodeEvent> {
    private final static ContractEventDefinition DEFINITION = new ContractEventDefinition(
            "eosio::setcode", // it just a stub event name, do not change it!
            Collections.emptyList()
    );

    @Override
    public SetCodeEvent build(String address, List<Object> values) {
        return new SetCodeEvent(DEFINITION, address);
    }

    @Override
    public ContractEventDefinition getDefinition() {
        return DEFINITION;
    }
}
