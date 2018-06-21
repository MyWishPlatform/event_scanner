package io.lastwill.eventscan.events.model.contract.crowdsale;


import io.lastwill.eventscan.events.model.contract.BaseEmptyEvent;
import io.mywish.wrapper.ContractEventDefinition;
import io.mywish.wrapper.WrapperTransactionReceipt;

public class FinalizedEvent extends BaseEmptyEvent {
    public FinalizedEvent(ContractEventDefinition definition, WrapperTransactionReceipt transactionReceipt, String address) {
        super(definition, transactionReceipt, address);
    }
}
