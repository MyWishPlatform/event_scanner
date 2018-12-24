package io.mywish.tron.blockchain.builders;

import io.lastwill.eventscan.events.model.contract.MintEvent;
import io.mywish.blockchain.ContractEventDefinition;
import io.mywish.troncli4j.model.EventResult;
import org.springframework.stereotype.Component;

import java.math.BigInteger;

@Component
public class MintTronEventBuilder extends TronEventBuilder<MintEvent> {
    private final static ContractEventDefinition DEFINITION = new ContractEventDefinition("Mint");

    @Override
    public MintEvent build(String address, EventResult event) {
        return new MintEvent(
                DEFINITION,
                event.getResult().get("to"),
                new BigInteger(event.getResult().get("amount")),
                address
        );
    }

    @Override
    public ContractEventDefinition getDefinition() {
        return DEFINITION;
    }
}