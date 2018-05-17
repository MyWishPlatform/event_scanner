package io.lastwill.eventscan.events;

import io.lastwill.eventscan.model.AddressLock;
import io.mywish.scanner.WrapperTransaction;
import io.mywish.scanner.WrapperTransactionReceipt;
import io.mywish.scanner.model.BaseEvent;
import io.mywish.scanner.model.NetworkType;
import lombok.Getter;

@Getter
public class TransactionUnlockedEvent extends BaseEvent {
    private final AddressLock addressLock;
    private final WrapperTransaction transaction;
    private final WrapperTransactionReceipt transactionReceipt;

    public TransactionUnlockedEvent(NetworkType networkType, AddressLock addressLock, WrapperTransaction transaction, WrapperTransactionReceipt transactionReceipt) {
        super(networkType);
        this.addressLock = addressLock;
        this.transaction = transaction;
        this.transactionReceipt = transactionReceipt;
    }
}
