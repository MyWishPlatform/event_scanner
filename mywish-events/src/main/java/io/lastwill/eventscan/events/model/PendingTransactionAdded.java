package io.lastwill.eventscan.events.model;

import io.lastwill.eventscan.model.NetworkType;
import io.mywish.wrapper.WrapperTransaction;
import lombok.Getter;

@Getter
public class PendingTransactionAdded extends BaseEvent {
    private final WrapperTransaction transaction;

    public PendingTransactionAdded(NetworkType networkType, WrapperTransaction transaction) {
        super(networkType);
        this.transaction = transaction;
    }
}
