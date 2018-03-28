package io.lastwill.eventscan.events;

import io.lastwill.eventscan.model.AddressLock;
import io.mywish.scanner.model.BaseEvent;
import io.mywish.scanner.model.NetworkType;
import lombok.Getter;
import org.web3j.protocol.core.methods.response.Transaction;
import org.web3j.protocol.core.methods.response.TransactionReceipt;

@Getter
public class TransactionUnlockedEvent extends BaseEvent {
    private final AddressLock addressLock;
    private final Transaction transaction;
    private final TransactionReceipt transactionReceipt;

    public TransactionUnlockedEvent(NetworkType networkType, AddressLock addressLock, Transaction transaction, TransactionReceipt transactionReceipt) {
        super(networkType);
        this.addressLock = addressLock;
        this.transaction = transaction;
        this.transactionReceipt = transactionReceipt;
    }
}
