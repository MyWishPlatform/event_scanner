package io.lastwill.eventscan.services.builders;

import io.lastwill.eventscan.events.contract.ContractEventDefinition;
import io.lastwill.eventscan.events.contract.InitializedEvent;
import io.lastwill.eventscan.events.contract.NotifiedEvent;
import org.springframework.stereotype.Component;
import org.web3j.protocol.core.methods.response.TransactionReceipt;

@Component
public class NotifiedEventBuilder extends BaseEmptyEventBuilder<NotifiedEvent> {
    public NotifiedEventBuilder() {
        super("Notified");
    }

    @Override
    protected NotifiedEvent buildEmpty(final ContractEventDefinition definition, String address, final TransactionReceipt transactionReceipt) {
        return new NotifiedEvent(definition, transactionReceipt, address);
    }
}
