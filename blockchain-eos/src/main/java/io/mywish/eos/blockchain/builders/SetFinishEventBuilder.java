package io.mywish.eos.blockchain.builders;

import com.fasterxml.jackson.databind.node.ObjectNode;
import io.lastwill.eventscan.events.model.contract.crowdsale.TimesChangedEvent;
import io.mywish.blockchain.ContractEventDefinition;
import org.springframework.stereotype.Component;

@Component
public class SetFinishEventBuilder extends ActionEventBuilder<TimesChangedEvent> {
    private static final ContractEventDefinition definition = new ContractEventDefinition(
            "setfinish"
    );

    @Override
    public TimesChangedEvent build(String address, ObjectNode data) {
        return new TimesChangedEvent(
                definition,
                address,
                null,
                data.get("finish").bigIntegerValue()
        );
    }

    @Override
    public ContractEventDefinition getDefinition() {
        return definition;
    }
}
