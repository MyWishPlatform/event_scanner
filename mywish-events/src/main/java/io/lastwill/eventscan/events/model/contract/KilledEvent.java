package io.lastwill.eventscan.events.model.contract;

import io.mywish.wrapper.ContractEvent;
import io.mywish.wrapper.ContractEventDefinition;
import io.mywish.wrapper.WrapperTransactionReceipt;
import lombok.Getter;

@Getter
public class KilledEvent extends ContractEvent {
    private final boolean byUser;
    public KilledEvent(ContractEventDefinition definition, boolean byUser, String address) {
        super(definition, address);
        this.byUser = byUser;
    }
}
