package io.lastwill.eventscan.events.contract;

import lombok.Getter;
import org.web3j.protocol.core.methods.response.TransactionReceipt;

@Getter
public class NeedRepeatCheckEvent extends ContractEvent {
    private final boolean isAccident;
    public NeedRepeatCheckEvent(ContractEventDefinition definition, TransactionReceipt transactionReceipt, boolean isAccident, String address) {
        super(definition, address, transactionReceipt);
        this.isAccident = isAccident;
    }
}
