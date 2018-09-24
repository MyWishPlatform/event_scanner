package io.mywish.eos.blockchain.builders;

import com.fasterxml.jackson.databind.node.ObjectNode;
import io.lastwill.eventscan.events.model.CreateTokenEvent;
import io.mywish.blockchain.ContractEventDefinition;
import io.mywish.eos.blockchain.helper.MaxSupplyParser;
import io.mywish.eos.blockchain.model.MaxSupply;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
@NoArgsConstructor
public class CreateTokenEventBuilder extends ActionEventBuilder<CreateTokenEvent> {
    private final static ContractEventDefinition DEFINITION = new ContractEventDefinition(
            "create" // it just a stub event name, do not change it!
    );

    @Autowired
    private MaxSupplyParser maxSupplyParser;

    @Override
    public CreateTokenEvent build(String address, ObjectNode node) {
        MaxSupply maxSupply = maxSupplyParser.parse(node.get("maximum_supply").textValue());
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