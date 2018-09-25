package io.lastwill.eventscan.events.model;

import io.lastwill.eventscan.model.NetworkType;
import io.mywish.blockchain.WrapperTransaction;
import lombok.Getter;

@Getter
public class PendingTransactionAddedEvent extends BaseEvent {
    private final WrapperTransaction transaction;

    public PendingTransactionAddedEvent(NetworkType networkType, WrapperTransaction transaction) {
        super(networkType);
        this.transaction = transaction;
    }
}
