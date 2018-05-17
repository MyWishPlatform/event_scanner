package io.lastwill.eventscan.events.contract;

import io.mywish.scanner.WrapperTransactionReceipt;
import lombok.Getter;

@Getter
public class KilledEvent extends ContractEvent {
    private final boolean byUser;
    public KilledEvent(ContractEventDefinition definition, WrapperTransactionReceipt transactionReceipt, boolean byUser, String address) {
        super(definition, address, transactionReceipt);
        this.byUser = byUser;
    }
}
