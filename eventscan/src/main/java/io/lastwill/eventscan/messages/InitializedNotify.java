package io.lastwill.eventscan.messages;

public class InitializedNotify extends NotifyContract {
    public InitializedNotify(int contractId, String txHash) {
        super(contractId, PaymentStatus.COMMITTED, txHash);
    }

    @Override
    public String getType() {
        return "initialized";
    }
}
