package io.lastwill.eventscan.messages;

import lombok.ToString;

@ToString(callSuper = true)
public class CheckedNotify extends NotifyContract {
    private final String transactionHash;

    public CheckedNotify(int contractId, String transactionHash, PaymentStatus status) {
        super(contractId, status);
        this.transactionHash = transactionHash;
    }

    @Override
    public String getType() {
        return "checked";
    }
}
