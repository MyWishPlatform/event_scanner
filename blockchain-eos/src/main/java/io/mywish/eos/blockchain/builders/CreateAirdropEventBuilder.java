package io.mywish.eos.blockchain.builders;

import com.fasterxml.jackson.databind.node.ObjectNode;
import io.lastwill.eventscan.events.model.CreateAirdropEvent;
import io.lastwill.eventscan.events.model.CreateTokenEvent;
import io.mywish.blockchain.ContractEventDefinition;
import io.mywish.eos.blockchain.helper.EosStructureParser;
import io.mywish.eos.blockchain.model.EosActionFieldsDefinition;
import io.mywish.eos.blockchain.model.MaxSupply;
import io.mywish.eos.blockchain.model.Symbol;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;

@Component
@NoArgsConstructor
public class CreateAirdropEventBuilder extends ActionEventBuilder<CreateAirdropEvent> {
    private final static ContractEventDefinition DEFINITION = new EosActionFieldsDefinition(
            "create", // it just a stub event name, do not change it!
            new HashSet<>(Arrays.asList("issuer", "token_contract", "symbol"))
    );

    @Autowired
    private EosStructureParser eosStructureParser;

    @Override
    public CreateAirdropEvent build(String address, ObjectNode node) {
        Symbol symbol = eosStructureParser.parseSymbol(node.get("symbol").textValue());
        return new CreateAirdropEvent(
                DEFINITION,
                address,
                node.get("issuer").textValue(),
                symbol.getSymbol(),
                node.get("token_contract").textValue(),
                symbol.getDecimals()
        );
    }

    @Override
    public ContractEventDefinition getDefinition() {
        return DEFINITION;
    }
}