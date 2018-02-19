package io.lastwill.eventscan.events.contract;

import lombok.Getter;
import org.web3j.protocol.core.methods.response.TransactionReceipt;

@Getter
public class CheckedEvent extends ContractEvent {
    private final boolean isAccident;
    public CheckedEvent(ContractEventDefinition definition, TransactionReceipt transactionReceipt, boolean isAccident, String address) {
        super(definition, address, transactionReceipt);
        this.isAccident = isAccident;
    }
}
