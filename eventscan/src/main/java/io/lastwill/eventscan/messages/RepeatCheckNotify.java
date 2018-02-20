package io.lastwill.eventscan.messages;

import lombok.ToString;

@ToString(callSuper = true)
public class RepeatCheckNotify extends NotifyContract {
    public RepeatCheckNotify(int contractId, String txHash) {
        super(contractId, PaymentStatus.COMMITTED, txHash);
    }

    @Override
    public String getType() {
        return "repeatCheck";
    }
}
