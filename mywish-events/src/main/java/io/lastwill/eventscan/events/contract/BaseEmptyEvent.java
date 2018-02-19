package io.lastwill.eventscan.events.contract;

import org.web3j.protocol.core.methods.response.TransactionReceipt;

public class BaseEmptyEvent extends ContractEvent {
    public BaseEmptyEvent(ContractEventDefinition definition, TransactionReceipt transactionReceipt, String address) {
        super(definition, address, transactionReceipt);
    }
}