package io.lastwill.eventscan.services.builders;

import io.lastwill.eventscan.events.contract.ContractEventDefinition;
import io.lastwill.eventscan.events.contract.InitializedEvent;
import org.springframework.stereotype.Component;
import org.web3j.protocol.core.methods.response.TransactionReceipt;

@Component
public class InitializedEventBuilder extends BaseEmptyEventBuilder<InitializedEvent> {
    public InitializedEventBuilder() {
        super("Initialized");
    }

    @Override
    protected InitializedEvent buildEmpty(final ContractEventDefinition definition, String address, final TransactionReceipt transactionReceipt) {
        return new InitializedEvent(definition, transactionReceipt, address);
    }
}
