package io.lastwill.eventscan.services.builders;

import io.lastwill.eventscan.events.contract.ContractEventDefinition;
import io.lastwill.eventscan.events.contract.FinalizedEvent;
import org.springframework.stereotype.Component;
import org.web3j.protocol.core.methods.response.TransactionReceipt;

@Component
public class FinalizedEventBuilder extends BaseEmptyEventBuilder<FinalizedEvent> {
    public FinalizedEventBuilder() {
        super("Finalized");
    }

    @Override
    protected FinalizedEvent buildEmpty(final ContractEventDefinition definition, String address, final TransactionReceipt transactionReceipt) {
        return new FinalizedEvent(definition, transactionReceipt, address);
    }
}
