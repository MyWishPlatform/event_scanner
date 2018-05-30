package io.lastwill.eventscan.services.builders;

import io.mywish.wrapper.WrapperTransactionReceipt;
import io.mywish.wrapper.ContractEventDefinition;
import io.lastwill.eventscan.events.contract.FinalizedEvent;
import org.springframework.stereotype.Component;

@Component
public class FinalizedEventBuilder extends BaseEmptyEventBuilder<FinalizedEvent> {
    public FinalizedEventBuilder() {
        super("Finalized");
    }

    @Override
    protected FinalizedEvent buildEmpty(final ContractEventDefinition definition, String address, final WrapperTransactionReceipt transactionReceipt) {
        return new FinalizedEvent(definition, transactionReceipt, address);
    }
}
