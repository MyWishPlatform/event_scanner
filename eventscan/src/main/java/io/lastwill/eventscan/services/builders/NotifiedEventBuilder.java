package io.lastwill.eventscan.services.builders;

import io.lastwill.eventscan.events.contract.ContractEventDefinition;
import io.lastwill.eventscan.events.contract.NotifiedEvent;
import io.mywish.scanner.WrapperTransactionReceipt;
import org.springframework.stereotype.Component;

@Component
public class NotifiedEventBuilder extends BaseEmptyEventBuilder<NotifiedEvent> {
    public NotifiedEventBuilder() {
        super("Notified");
    }

    @Override
    protected NotifiedEvent buildEmpty(final ContractEventDefinition definition, String address, final WrapperTransactionReceipt transactionReceipt) {
        return new NotifiedEvent(definition, transactionReceipt, address);
    }
}
