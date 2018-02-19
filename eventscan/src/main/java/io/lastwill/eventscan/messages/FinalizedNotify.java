package io.lastwill.eventscan.messages;

public class FinalizedNotify extends NotifyContract {
    public FinalizedNotify(int contractId, String txHash) {
        super(contractId, PaymentStatus.COMMITTED, txHash);
    }

    @Override
    public String getType() {
        return "finalized";
    }
}
