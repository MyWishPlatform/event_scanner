package io.mywish.eos.blockchain.builders;

import com.fasterxml.jackson.databind.node.ObjectNode;
import io.lastwill.eventscan.events.TypeHelper;
import io.lastwill.eventscan.events.model.contract.eos.EosTransferEvent;
import io.mywish.blockchain.ContractEventDefinition;
import io.mywish.eos.blockchain.helper.EosStructureParser;
import io.mywish.eos.blockchain.model.MaxSupply;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
@Getter
@NoArgsConstructor
public class EosTransferEventBuilder extends ActionEventBuilder<EosTransferEvent> {
    @Autowired
    private TypeHelper typeHelper;

    @Autowired
    private EosStructureParser parser;

    private final ContractEventDefinition definition = new ContractEventDefinition(
            "transfer"
    );

    @Override
    public EosTransferEvent build(String address, ObjectNode data) {
        MaxSupply maxSupply = parser.parseMaxSupply(data.get("quantity").textValue());
        return new EosTransferEvent(
                definition,
                data.get("from").textValue(),
                data.get("to").textValue(),
                maxSupply.getIntegerValue(),
                typeHelper.toBytesArray(data.get("memo").textValue()),
                maxSupply.getSymbol(),
                address
        );
    }
}
