package io.lastwill.eventscan.messages;

import lombok.ToString;

@ToString(callSuper = true)
public class ContractKilledNotify extends NotifyContract {
    public ContractKilledNotify(int contractId, PaymentStatus status) {
        super(contractId, status);
    }

    @Override
    public String getType() {
        return "killed";
    }
}
