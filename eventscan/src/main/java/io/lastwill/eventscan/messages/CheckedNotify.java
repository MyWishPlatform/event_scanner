package io.lastwill.eventscan.messages;

import lombok.ToString;

@ToString(callSuper = true)
public class CheckedNotify extends NotifyContract {
    public CheckedNotify(int contractId, String transactionHash) {
        super(contractId, PaymentStatus.COMMITTED, transactionHash);
    }

    @Override
    public String getType() {
        return "checked";
    }
}
