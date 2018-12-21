package io.mywish.tron.blockchain.builders;

import io.lastwill.eventscan.events.model.contract.OwnershipTransferredEvent;
import io.mywish.blockchain.ContractEventDefinition;
import io.mywish.troncli4j.model.EventResult;
import org.springframework.stereotype.Component;

@Component
public class OwnershipTransferredTronEventBuilder extends TronEventBuilder<OwnershipTransferredEvent> {
    private final static ContractEventDefinition DEFINITION = new ContractEventDefinition("OwnershipTransferred");

    @Override
    public OwnershipTransferredEvent build(String address, EventResult event) {
        return new OwnershipTransferredEvent(
                DEFINITION,
                event.getResult().get("previousOwner"),
                event.getResult().get("newOwner"),
                address
        );
    }

    @Override
    public ContractEventDefinition getDefinition() {
        return DEFINITION;
    }
}