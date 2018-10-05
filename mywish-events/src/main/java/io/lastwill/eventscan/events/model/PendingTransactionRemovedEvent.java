package io.lastwill.eventscan.events.model;

import io.lastwill.eventscan.model.NetworkType;
import io.mywish.blockchain.WrapperTransaction;
import lombok.Getter;

@Getter
public class PendingTransactionRemovedEvent extends BaseEvent {
    private final WrapperTransaction transaction;
    private final Reason reason;
    private final Long blockNumber;

    public PendingTransactionRemovedEvent(NetworkType networkType, WrapperTransaction transaction, Reason reason, Long blockNumber) {
        super(networkType);
        this.transaction = transaction;
        this.reason = reason;
        this.blockNumber = blockNumber;
    }

    public enum Reason {
//        REJECTED,
        /**
         * Transaction was timed out by our system in pending list.
         */
        TIMEOUT,
        /**
         * Transaction was accepted to the block.
         */
        ACCEPTED,
    }
}
