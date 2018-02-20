package io.lastwill.eventscan.events;

import io.lastwill.eventscan.model.AddressLock;
import io.mywish.scanner.BaseEvent;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.web3j.protocol.core.methods.response.Transaction;
import org.web3j.protocol.core.methods.response.TransactionReceipt;

@Getter
@RequiredArgsConstructor
public class TransactionUnlockedEvent extends BaseEvent {
    private final AddressLock addressLock;
    private final Transaction transaction;
    private final TransactionReceipt transactionReceipt;
}
