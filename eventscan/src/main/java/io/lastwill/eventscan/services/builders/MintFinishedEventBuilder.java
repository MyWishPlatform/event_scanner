package io.lastwill.eventscan.services.builders;

import io.lastwill.eventscan.events.contract.ContractEventDefinition;
import io.lastwill.eventscan.events.contract.MintFinishedEvent;
import io.mywish.scanner.WrapperTransactionReceipt;
import org.springframework.stereotype.Component;

@Component
public class MintFinishedEventBuilder extends BaseEmptyEventBuilder<MintFinishedEvent> {
    public MintFinishedEventBuilder() {
        super("MintFinished");
    }

    @Override
    protected MintFinishedEvent buildEmpty(final ContractEventDefinition definition, String address, final WrapperTransactionReceipt transactionReceipt) {
        return new MintFinishedEvent(definition, transactionReceipt, address);
    }
}
