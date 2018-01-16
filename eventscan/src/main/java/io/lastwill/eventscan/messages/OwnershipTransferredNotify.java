package io.lastwill.eventscan.messages;

public class OwnershipTransferredNotify extends NotifyContract {
    public OwnershipTransferredNotify(int contractId, String txHash) {
        super(contractId, PaymentStatus.COMMITTED, txHash);
    }

    @Override
    public String getType() {
        return "ownershipTransferred";
    }
}
