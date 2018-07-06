package io.lastwill.eventscan.events.builders;

import io.mywish.wrapper.ContractEventDefinition;
import io.lastwill.eventscan.events.model.contract.MintFinishedEvent;
import org.springframework.stereotype.Component;

@Component
public class MintFinishedEventBuilder extends BaseEmptyEventBuilder<MintFinishedEvent> {
    public MintFinishedEventBuilder() {
        super("MintFinished");
    }

    @Override
    protected MintFinishedEvent buildEmpty(final ContractEventDefinition definition, String address) {
        return new MintFinishedEvent(definition, address);
    }
}
