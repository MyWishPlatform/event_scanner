package io.mywish.web3.blockchain.builders;

import io.lastwill.eventscan.events.model.contract.NotifiedEvent;
import io.mywish.blockchain.ContractEventDefinition;
import org.springframework.stereotype.Component;

@Component
public class NotifiedEventBuilder extends BaseEmptyEventBuilder<NotifiedEvent> {
    public NotifiedEventBuilder() {
        super("Notified");
    }

    @Override
    protected NotifiedEvent buildEmpty(final ContractEventDefinition definition, String address) {
        return new NotifiedEvent(definition, address);
    }
}
