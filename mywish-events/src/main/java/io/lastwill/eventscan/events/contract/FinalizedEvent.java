package io.lastwill.eventscan.events.contract;

import org.web3j.protocol.core.methods.response.TransactionReceipt;

public class FinalizedEvent extends BaseEmptyEvent {
    public FinalizedEvent(ContractEventDefinition definition, TransactionReceipt transactionReceipt, String address) {
        super(definition, transactionReceipt, address);
    }
}
