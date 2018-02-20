package io.lastwill.eventscan.messages;

import lombok.Getter;

@Getter
public class OwnershipTransferredNotify extends NotifyContract {
    private final int crowdsaleId;

    public OwnershipTransferredNotify(int contractId, String txHash, int crowdsaleContractId) {
        super(contractId, PaymentStatus.COMMITTED, txHash);
        this.crowdsaleId = crowdsaleContractId;
    }

    @Override
    public String getType() {
        return "ownershipTransferred";
    }
}
