package io.lastwill.eventscan.services.builders;

import io.lastwill.eventscan.events.contract.ContractEventDefinition;
import io.lastwill.eventscan.events.contract.MintFinishedEvent;
import org.springframework.stereotype.Component;
import org.web3j.protocol.core.methods.response.TransactionReceipt;

@Component
public class MintFinishedEventBuilder extends BaseEmptyEventBuilder<MintFinishedEvent> {
    public MintFinishedEventBuilder() {
        super("MintFinished");
    }

    @Override
    protected MintFinishedEvent buildEmpty(final ContractEventDefinition definition, String address, final TransactionReceipt transactionReceipt) {
        return new MintFinishedEvent(definition, transactionReceipt, address);
    }
}
