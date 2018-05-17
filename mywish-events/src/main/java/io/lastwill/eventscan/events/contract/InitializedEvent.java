package io.lastwill.eventscan.events.contract;

import io.mywish.scanner.WrapperTransactionReceipt;

public class InitializedEvent extends BaseEmptyEvent {
    public InitializedEvent(ContractEventDefinition definition, WrapperTransactionReceipt transactionReceipt, String address) {
        super(definition, transactionReceipt, address);
    }
}
