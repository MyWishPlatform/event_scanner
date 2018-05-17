package io.lastwill.eventscan.events.contract;

import io.mywish.scanner.WrapperTransactionReceipt;

public class MintFinishedEvent extends BaseEmptyEvent {
    public MintFinishedEvent(ContractEventDefinition definition, WrapperTransactionReceipt transactionReceipt, String address) {
        super(definition, transactionReceipt, address);
    }
}
