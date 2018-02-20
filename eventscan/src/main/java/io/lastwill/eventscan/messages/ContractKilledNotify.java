package io.lastwill.eventscan.messages;

import lombok.ToString;

@ToString(callSuper = true)
public class ContractKilledNotify extends NotifyContract {
    public ContractKilledNotify(int contractId, String txHash) {
        super(contractId, PaymentStatus.COMMITTED, txHash);
    }

    @Override
    public String getType() {
        return "killed";
    }
}
