package io.lastwill.eventscan.events.builders.crowdsale;

import io.lastwill.eventscan.events.builders.BaseEmptyEventBuilder;
import io.mywish.wrapper.ContractEventDefinition;
import io.lastwill.eventscan.events.model.contract.crowdsale.FinalizedEvent;
import org.springframework.stereotype.Component;

@Component
public class FinalizedEventBuilder extends BaseEmptyEventBuilder<FinalizedEvent> {
    public FinalizedEventBuilder() {
        super("Finalized");
    }

    @Override
    protected FinalizedEvent buildEmpty(final ContractEventDefinition definition, String address) {
        return new FinalizedEvent(definition, address);
    }
}
