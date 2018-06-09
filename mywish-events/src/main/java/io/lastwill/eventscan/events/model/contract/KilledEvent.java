package io.lastwill.eventscan.events.model.contract;

import io.mywish.wrapper.ContractEvent;
import io.mywish.wrapper.ContractEventDefinition;
import io.mywish.wrapper.WrapperTransactionReceipt;
import lombok.Getter;

@Getter
public class KilledEvent extends ContractEvent {
    private final boolean byUser;
    public KilledEvent(ContractEventDefinition definition, WrapperTransactionReceipt transactionReceipt, boolean byUser, String address) {
        super(definition, address, transactionReceipt);
        this.byUser = byUser;
    }
}
