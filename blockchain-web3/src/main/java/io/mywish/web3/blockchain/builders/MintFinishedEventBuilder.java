package io.mywish.web3.blockchain.builders;

import io.lastwill.eventscan.events.model.contract.MintFinishedEvent;
import io.mywish.blockchain.ContractEventDefinition;
import org.springframework.stereotype.Component;

@Component
public class MintFinishedEventBuilder extends BaseEmptyEventBuilder<MintFinishedEvent> {
    public MintFinishedEventBuilder() {
        super("MintFinished");
    }

    @Override
    protected MintFinishedEvent buildEmpty(final ContractEventDefinition definition, String address) {
        return new MintFinishedEvent(definition, address);
    }
}
