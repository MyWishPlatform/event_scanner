package io.lastwill.eventscan.events.model.contract;

import io.mywish.wrapper.ContractEventDefinition;
import io.mywish.wrapper.WrapperTransactionReceipt;

public class MintFinishedEvent extends BaseEmptyEvent {
    public MintFinishedEvent(ContractEventDefinition definition, String address) {
        super(definition, address);
    }
}
