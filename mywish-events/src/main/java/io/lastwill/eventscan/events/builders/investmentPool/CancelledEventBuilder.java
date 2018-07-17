package io.lastwill.eventscan.events.builders.investmentPool;

import io.lastwill.eventscan.events.builders.BaseEmptyEventBuilder;
import io.lastwill.eventscan.events.model.contract.investmentPool.CancelledEvent;
import io.mywish.wrapper.ContractEventDefinition;

public class CancelledEventBuilder extends BaseEmptyEventBuilder<CancelledEvent> {
    public CancelledEventBuilder(String eventName) {
        super(eventName);
    }

    @Override
    protected CancelledEvent buildEmpty(ContractEventDefinition definition, String address) {
        return new CancelledEvent(definition, address);
    }
}
