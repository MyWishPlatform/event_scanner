package io.lastwill.eventscan.messages;

import lombok.ToString;

@ToString(callSuper = true)
public class RepeatCheckNotify extends NotifyContract {
    public RepeatCheckNotify(int contractId, PaymentStatus status) {
        super(contractId, status);
    }

    @Override
    public String getType() {
        return "repeatCheck";
    }
}
