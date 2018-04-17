package io.lastwill.eventscan.messages;

import lombok.ToString;

@ToString(callSuper = true)
public class NotifiedNotify extends NotifyContract {
    public NotifiedNotify(int contractId, String transactionHash) {
        super(contractId, PaymentStatus.COMMITTED, transactionHash);
    }

    @Override
    public String getType() {
        return "notified";
    }
}
