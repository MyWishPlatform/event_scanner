package io.lastwill.eventscan.messages;

import lombok.Getter;

import java.math.BigInteger;

@Getter
public class RefundNotify extends NotifyContract {
    private final BigInteger amount;

    public RefundNotify(int contractId, PaymentStatus status, String txHash, BigInteger amount) {
        super(contractId, status, txHash);
        this.amount = amount;
    }

    @Override
    public String getType() {
        return "refund";
    }
}
