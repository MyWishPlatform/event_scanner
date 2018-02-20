package io.lastwill.eventscan.events.contract;

import lombok.Getter;
import org.web3j.protocol.core.methods.response.TransactionReceipt;

@Getter
public class KilledEvent extends ContractEvent {
    private final boolean byUser;
    public KilledEvent(ContractEventDefinition definition, TransactionReceipt transactionReceipt, boolean byUser, String address) {
        super(definition, address, transactionReceipt);
        this.byUser = byUser;
    }
}
