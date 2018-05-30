package io.lastwill.eventscan.events.contract;

import io.mywish.wrapper.ContractEventDefinition;
import io.mywish.wrapper.WrapperTransactionReceipt;

public class NotifiedEvent extends BaseEmptyEvent {
    public NotifiedEvent(ContractEventDefinition definition, WrapperTransactionReceipt transactionReceipt, String address) {
        super(definition, transactionReceipt, address);
    }
}
