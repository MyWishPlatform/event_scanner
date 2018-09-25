package io.lastwill.eventscan.events.model.contract;

import io.mywish.blockchain.ContractEvent;
import io.mywish.blockchain.ContractEventDefinition;
import io.mywish.blockchain.WrapperTransactionReceipt;
import lombok.Getter;

@Getter
public class NeedRepeatCheckEvent extends ContractEvent {
    private final boolean isAccident;
    public NeedRepeatCheckEvent(ContractEventDefinition definition, WrapperTransactionReceipt transactionReceipt, boolean isAccident, String address) {
        super(definition, address);
        this.isAccident = isAccident;
    }
}
