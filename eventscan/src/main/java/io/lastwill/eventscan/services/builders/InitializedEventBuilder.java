package io.lastwill.eventscan.services.builders;

import io.lastwill.eventscan.events.contract.ContractEventDefinition;
import io.lastwill.eventscan.events.contract.InitializedEvent;
import io.mywish.scanner.WrapperTransactionReceipt;
import org.springframework.stereotype.Component;

@Component
public class InitializedEventBuilder extends BaseEmptyEventBuilder<InitializedEvent> {
    public InitializedEventBuilder() {
        super("Initialized");
    }

    @Override
    protected InitializedEvent buildEmpty(final ContractEventDefinition definition, String address, final WrapperTransactionReceipt transactionReceipt) {
        return new InitializedEvent(definition, transactionReceipt, address);
    }
}
