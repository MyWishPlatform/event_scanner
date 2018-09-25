package io.mywish.eos.blockchain.builders;

import com.fasterxml.jackson.databind.node.ObjectNode;
import io.lastwill.eventscan.events.model.SetCodeEvent;
import io.mywish.eos.blockchain.model.EosActionDefinition;
import lombok.NoArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@NoArgsConstructor
public class SetCodeEventBuilder extends ActionEventBuilder<SetCodeEvent> {
    private final static EosActionDefinition DEFINITION = new EosActionDefinition(
            "setcode",
            "eosio"
    );

    @Override
    public SetCodeEvent build(String address, ObjectNode data) {
        return new SetCodeEvent(DEFINITION, address);
    }

    @Override
    public EosActionDefinition getDefinition() {
        return DEFINITION;
    }
}
