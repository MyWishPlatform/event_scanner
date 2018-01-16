package io.lastwill.eventscan.messages;

import lombok.ToString;

@ToString(callSuper = true)
public class ContractTriggeredNotify extends NotifyContract {
    public ContractTriggeredNotify(int contractId, String txHash) {
        super(contractId, PaymentStatus.COMMITTED, txHash);
    }

    @Override
    public String getType() {
        return "triggered";
    }
}
