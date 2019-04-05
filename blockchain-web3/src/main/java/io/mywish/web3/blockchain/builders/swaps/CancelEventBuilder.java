package io.mywish.web3.blockchain.builders.swaps;

import io.lastwill.eventscan.events.model.contract.swaps.CancelEvent;
import io.mywish.blockchain.ContractEventDefinition;
import io.mywish.web3.blockchain.builders.BaseEmptyEventBuilder;
import org.springframework.stereotype.Component;

@Component
public class CancelEventBuilder extends BaseEmptyEventBuilder<CancelEvent> {
    public CancelEventBuilder() {
        super("Cancel");
    }

    @Override
    protected CancelEvent buildEmpty(ContractEventDefinition definition, String address) {
        return new CancelEvent(definition, address);
    }
}
