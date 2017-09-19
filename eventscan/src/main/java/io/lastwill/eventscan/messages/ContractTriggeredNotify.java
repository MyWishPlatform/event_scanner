package io.lastwill.eventscan.messages;

import lombok.ToString;

@ToString(callSuper = true)
public class ContractTriggeredNotify extends NotifyContract {
    public ContractTriggeredNotify(int contractId, PaymentStatus status) {
        super(contractId, status);
    }

    @Override
    public String getType() {
        return "triggered";
    }
}
