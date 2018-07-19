package io.lastwill.eventscan.messages;

public class CancelledNotify extends NotifyContract {
    public CancelledNotify(int contractId, PaymentStatus status, String txHash) {
        super(contractId, status, txHash);
    }

    @Override
    public String getType() {
        return "cancelled";
    }
}
