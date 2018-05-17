package io.lastwill.eventscan.events.contract;

import io.mywish.scanner.WrapperTransactionReceipt;

public class BaseEmptyEvent extends ContractEvent {
    public BaseEmptyEvent(ContractEventDefinition definition, WrapperTransactionReceipt transactionReceipt, String address) {
        super(definition, address, transactionReceipt);
    }
}