package io.lastwill.eventscan.messages;

import lombok.ToString;

import java.math.BigInteger;

@ToString(callSuper = true)
public class FundsAddedNotify extends NotifyContract {
    private final BigInteger value;
    private final BigInteger balance;

    public FundsAddedNotify(int contractId, String transactionHash, BigInteger value, BigInteger balance) {
        super(contractId, PaymentStatus.COMMITTED, transactionHash);
        this.value = value;
        this.balance = balance;
    }

    @Override
    public String getType() {
        return "fundsAdded";
    }
}
