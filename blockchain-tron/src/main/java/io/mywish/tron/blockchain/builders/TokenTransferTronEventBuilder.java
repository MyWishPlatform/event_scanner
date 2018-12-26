package io.mywish.tron.blockchain.builders;

import io.lastwill.eventscan.events.model.contract.erc20.TransferEvent;
import io.mywish.blockchain.ContractEventDefinition;
import io.mywish.troncli4j.model.EventResult;
import org.springframework.stereotype.Component;

import java.math.BigInteger;

@Component
public class TokenTransferTronEventBuilder extends TronEventBuilder<TransferEvent> {
    private final static ContractEventDefinition DEFINITION = new ContractEventDefinition("Transfer");

    @Override
    public TransferEvent build(String address, EventResult event) {
        return new TransferEvent(
                DEFINITION,
                event.getResult().get("from"),
                event.getResult().get("to"),
                new BigInteger(event.getResult().get("value")),
                address
        );
    }

    @Override
    public ContractEventDefinition getDefinition() {
        return DEFINITION;
    }
}