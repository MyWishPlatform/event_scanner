package io.mywish.eos.blockchain.builders;

import com.fasterxml.jackson.databind.node.ObjectNode;
import io.lastwill.eventscan.events.model.contract.CreateAccountEvent;
import io.mywish.eos.blockchain.model.EosActionAccountDefinition;
import org.springframework.stereotype.Component;

@Component
public class CreateAccountEventBuilder extends ActionEventBuilder<CreateAccountEvent> {
    private final static EosActionAccountDefinition DEFINITION = new EosActionAccountDefinition(
            "newaccount",
            "eosio"
    );

    public CreateAccountEventBuilder() {
    }

    @Override
    public CreateAccountEvent build(String address, ObjectNode objectNode) {
        String newAccountName;
        if (objectNode.has("name")) {
            newAccountName = objectNode.get("name").textValue();
        }
        else if (objectNode.has("newact")) {
            newAccountName = objectNode.get("newact").textValue();
        }
        else {
            throw new IllegalStateException("Event parameters does not contain not neme or newact values.");
        }
        return new CreateAccountEvent(
                DEFINITION,
                address,
                objectNode.get("creator").textValue(),
                newAccountName
        );
    }

    @Override
    public EosActionAccountDefinition getDefinition() {
        return DEFINITION;
    }
}