package io.lastwill.eventscan.events.model;

import io.lastwill.eventscan.model.NetworkType;
import io.mywish.wrapper.WrapperTransaction;
import lombok.Getter;

import javax.annotation.Nullable;

@Getter
public class PendingTransactionRemoved extends BaseEvent {
    private final WrapperTransaction transaction;
    private final Reason reason;
    @Nullable
    private final Long blockNumber;

    public PendingTransactionRemoved(NetworkType networkType, WrapperTransaction transaction, Reason reason, @Nullable Long blockNumber) {
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
