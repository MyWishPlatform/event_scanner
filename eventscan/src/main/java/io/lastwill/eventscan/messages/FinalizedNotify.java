package io.lastwill.eventscan.messages;

public class FinalizedNotify extends NotifyContract {
    public FinalizedNotify(int contractId, String txHash) {
        super(contractId, PaymentStatus.COMMITTED, txHash);
    }

    public FinalizedNotify(int contractId, PaymentStatus status, String txHash) {
        super(contractId, status, txHash);
    }

    @Override
    public String getType() {
        return "finalized";
    }
}
