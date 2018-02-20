package io.lastwill.eventscan.messages;

import lombok.Getter;

@Getter
public class TransactionCompletedNotify extends BaseNotify {
    private final int addressId;
    private final String address;
    private final int lockedBy;
    private final boolean transactionStatus;

    public TransactionCompletedNotify(String transactionHash, int addressId, String address, int lockedBy, boolean transactionStatus) {
        super(PaymentStatus.COMMITTED, transactionHash);
        this.addressId = addressId;
        this.address = address;
        this.lockedBy = lockedBy;
        this.transactionStatus = transactionStatus;
    }

    @Override
    public String getType() {
        return "transactionCompleted";
    }
}
