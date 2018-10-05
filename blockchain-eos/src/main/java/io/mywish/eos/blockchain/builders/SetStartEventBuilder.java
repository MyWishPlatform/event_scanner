package io.mywish.eos.blockchain.builders;

import com.fasterxml.jackson.databind.node.ObjectNode;
import io.lastwill.eventscan.events.model.contract.crowdsale.TimesChangedEvent;
import io.mywish.blockchain.ContractEventDefinition;
import lombok.NoArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@NoArgsConstructor
public class SetStartEventBuilder extends ActionEventBuilder<TimesChangedEvent> {
    private static final ContractEventDefinition definition = new ContractEventDefinition(
            "setstart"
    );

    @Override
    public TimesChangedEvent build(String address, ObjectNode data) {
        return new TimesChangedEvent(
                definition,
                address,
                data.get("start").bigIntegerValue(),
                null
        );
    }

    @Override
    public ContractEventDefinition getDefinition() {
        return definition;
    }
}
