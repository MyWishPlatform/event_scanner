package io.lastwill.eventscan.events.contract;

import org.web3j.protocol.core.methods.response.TransactionReceipt;

public class NotifiedEvent extends BaseEmptyEvent {
    public NotifiedEvent(ContractEventDefinition definition, TransactionReceipt transactionReceipt, String address) {
        super(definition, transactionReceipt, address);
    }
}
