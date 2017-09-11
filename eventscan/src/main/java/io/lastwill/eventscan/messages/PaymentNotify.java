package io.lastwill.eventscan.messages;

import lombok.Getter;
import lombok.ToString;

import java.math.BigInteger;

@ToString(callSuper = true)
@Getter
public class PaymentNotify extends NotifyContract {
    private final BigInteger balance;

    public PaymentNotify(int contractId, BigInteger balance, PaymentStatus state) {
        super(contractId, state);
        this.balance = balance;
    }

    @Override
    public String getType() {
        return "payment";
    }
}
