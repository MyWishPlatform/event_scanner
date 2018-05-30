package io.lastwill.eventscan.events.contract;

import io.mywish.wrapper.ContractEvent;
import io.mywish.wrapper.ContractEventDefinition;
import io.mywish.wrapper.WrapperTransactionReceipt;
import lombok.Getter;

@Getter
public class CheckedEvent extends ContractEvent {
    private final boolean isAccident;
    public CheckedEvent(ContractEventDefinition definition, WrapperTransactionReceipt transactionReceipt, boolean isAccident, String address) {
        super(definition, address, transactionReceipt);
        this.isAccident = isAccident;
    }
}
