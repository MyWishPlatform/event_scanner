package io.mywish.eos.blockchain.builders;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import io.lastwill.eventscan.events.model.contract.AirdropEvent;
import io.mywish.blockchain.ContractEventDefinition;
import io.mywish.eos.blockchain.helper.EosStructureParser;
import io.mywish.eos.blockchain.model.EosActionFieldsDefinition;
import io.mywish.eos.blockchain.model.Symbol;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@Component
@NoArgsConstructor
public class AirdropEventBuilder extends ActionEventBuilder<AirdropEvent> {
    private final static ContractEventDefinition DEFINITION = new EosActionFieldsDefinition(
            "drop", // it just a stub event name, do not change it!
            Arrays.asList("issuer", "token_contract", "symbol", "addresses", "amounts")
    );

    @Autowired
    private EosStructureParser eosStructureParser;

    @Override
    public AirdropEvent build(String address, ObjectNode node) {
        Symbol symbol = eosStructureParser.parseSymbol(node.get("symbol").textValue());
        return new AirdropEvent(
                DEFINITION,
                address,
                node.get("issuer").textValue(),
                node.get("token_contract").textValue(),
                symbol.getSymbol(),
                StreamSupport.stream(node.get("addresses").spliterator(), false)
                        .map(JsonNode::textValue)
                        .collect(Collectors.toList()),
                StreamSupport.stream(node.get("amounts").spliterator(), false)
                        .map(JsonNode::bigIntegerValue)
                        .collect(Collectors.toList())
        );
    }

    @Override
    public ContractEventDefinition getDefinition() {
        return DEFINITION;
    }
}