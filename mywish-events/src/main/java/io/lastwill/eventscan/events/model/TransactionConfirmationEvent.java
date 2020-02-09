package io.lastwill.eventscan.events.model;

import io.lastwill.eventscan.model.NetworkType;
import io.mywish.blockchain.ContractEvent;
import io.mywish.blockchain.ContractEventDefinition;
import lombok.Getter;

@Getter
public class TransactionConfirmationEvent extends ContractEvent
{
    private final static ContractEventDefinition definition = new ContractEventDefinition("TransactionConfirmation");

    public TransactionConfirmationEvent( ContractEventDefinition definition, String address) {
        super(definition, address);

    }

}
