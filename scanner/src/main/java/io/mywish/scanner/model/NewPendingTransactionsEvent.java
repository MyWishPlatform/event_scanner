package io.mywish.scanner.model;

import io.lastwill.eventscan.events.model.BaseEvent;
import io.lastwill.eventscan.model.NetworkType;
import io.mywish.blockchain.WrapperTransaction;
import lombok.Getter;

import java.util.List;

@Getter
public class NewPendingTransactionsEvent extends BaseEvent {
    private final List<WrapperTransaction> pendingTransactions;
    public NewPendingTransactionsEvent(NetworkType networkType, List<WrapperTransaction> pendingTransactions) {
        super(networkType);
        this.pendingTransactions = pendingTransactions;
    }
}
