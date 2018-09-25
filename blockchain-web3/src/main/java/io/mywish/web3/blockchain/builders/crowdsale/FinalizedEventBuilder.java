package io.mywish.web3.blockchain.builders.crowdsale;

import io.lastwill.eventscan.events.model.contract.crowdsale.FinalizedEvent;
import io.mywish.blockchain.ContractEventDefinition;
import io.mywish.web3.blockchain.builders.BaseEmptyEventBuilder;
import org.springframework.stereotype.Component;

@Component
public class FinalizedEventBuilder extends BaseEmptyEventBuilder<FinalizedEvent> {
    public FinalizedEventBuilder() {
        super("Finalized");
    }

    @Override
    protected FinalizedEvent buildEmpty(final ContractEventDefinition definition, String address) {
        return new FinalizedEvent(definition, address);
    }
}
