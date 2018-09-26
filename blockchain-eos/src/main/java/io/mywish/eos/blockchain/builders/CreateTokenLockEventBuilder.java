package io.mywish.eos.blockchain.builders;

import com.fasterxml.jackson.databind.node.ObjectNode;
import io.lastwill.eventscan.events.model.CreateTokenEvent;
import io.mywish.blockchain.ContractEventDefinition;
import io.mywish.eos.blockchain.helper.EosStructureParser;
import io.mywish.eos.blockchain.model.EosActionFieldsDefinition;
import io.mywish.eos.blockchain.model.MaxSupply;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.HashSet;

@Component
@NoArgsConstructor
public class CreateTokenLockEventBuilder extends ActionEventBuilder<CreateTokenEvent> {
    private final static ContractEventDefinition DEFINITION = new EosActionFieldsDefinition(
            "create", // it just a stub event name, do not change it!
            new HashSet<>(Arrays.asList("issuer", "maximum_supply", "lock"))
    );

    @Autowired
    private EosStructureParser eosStructureParser;

    @Override
    public CreateTokenEvent build(String address, ObjectNode node) {
        MaxSupply maxSupply = eosStructureParser.parseMaxSupply(node.get("maximum_supply").textValue());
        return new CreateTokenEvent(
                DEFINITION,
                address,
                node.get("issuer").textValue(),
                maxSupply.getSymbol(),
                maxSupply.getValue(),
                maxSupply.getDecimals()
        );
    }

    @Override
    public ContractEventDefinition getDefinition() {
        return DEFINITION;
    }
}