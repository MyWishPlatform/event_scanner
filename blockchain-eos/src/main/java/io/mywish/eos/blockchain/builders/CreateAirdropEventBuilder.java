package io.mywish.eos.blockchain.builders;

import com.fasterxml.jackson.databind.node.ObjectNode;
import io.lastwill.eventscan.events.model.contract.eos.CreateAirdropEvent;
import io.mywish.blockchain.ContractEventDefinition;
import io.mywish.eos.blockchain.helper.EosStructureParser;
import io.mywish.eos.blockchain.model.EosActionFieldsDefinition;
import io.mywish.eos.blockchain.model.Symbol;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Arrays;

@Component
@NoArgsConstructor
public class CreateAirdropEventBuilder extends ActionEventBuilder<CreateAirdropEvent> {
    private final static ContractEventDefinition DEFINITION = new EosActionFieldsDefinition(
            "create", // it just a stub event name, do not change it!
            Arrays.asList("pk", "issuer", "token_contract", "symbol", "drops")
    );

    @Autowired
    private EosStructureParser eosStructureParser;

    @Override
    public CreateAirdropEvent build(String address, ObjectNode node) {
        Symbol symbol = eosStructureParser.parseSymbol(node.get("symbol").textValue());
        return new CreateAirdropEvent(
                DEFINITION,
                address,
                node.get("pk").longValue(),
                node.get("issuer").textValue(),
                symbol.getSymbol(),
                node.get("token_contract").textValue(),
                symbol.getDecimals(),
                node.get("drops").longValue()
        );
    }

    @Override
    public ContractEventDefinition getDefinition() {
        return DEFINITION;
    }
}