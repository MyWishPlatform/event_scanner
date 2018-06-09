package io.lastwill.eventscan.events.builders;

import io.mywish.wrapper.WrapperTransactionReceipt;
import io.mywish.wrapper.ContractEventDefinition;
import io.lastwill.eventscan.events.model.contract.InitializedEvent;
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
