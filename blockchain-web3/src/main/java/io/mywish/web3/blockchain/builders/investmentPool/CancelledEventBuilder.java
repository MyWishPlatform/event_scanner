package io.mywish.web3.blockchain.builders.investmentPool;

import io.lastwill.eventscan.events.model.contract.investmentPool.CancelledEvent;
import io.mywish.blockchain.ContractEventDefinition;
import io.mywish.web3.blockchain.builders.BaseEmptyEventBuilder;
import org.springframework.stereotype.Component;

@Component
public class CancelledEventBuilder extends BaseEmptyEventBuilder<CancelledEvent> {
    public CancelledEventBuilder() {
        super("Cancelled");
    }

    @Override
    protected CancelledEvent buildEmpty(ContractEventDefinition definition, String address) {
        return new CancelledEvent(definition, address);
    }
}
