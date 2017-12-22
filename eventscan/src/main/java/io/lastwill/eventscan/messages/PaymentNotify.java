package io.lastwill.eventscan.messages;

import io.lastwill.eventscan.model.CryptoCurrency;
import lombok.Getter;
import lombok.ToString;

import java.math.BigInteger;

@ToString(callSuper = true)
@Getter
public class PaymentNotify extends BaseNotify {
    private final int userId;
    private final BigInteger amount;
    private final String txHash;
    private final CryptoCurrency currency;
    private final boolean isSuccess;

    public PaymentNotify(int userId, BigInteger amount, PaymentStatus status, String txHash, CryptoCurrency currency, boolean isSuccess) {
        super(status);
        this.userId = userId;
        this.amount = amount;
        this.txHash = txHash;
        this.currency = currency;
        this.isSuccess = isSuccess;
    }

    @Override
    public String getType() {
        return "payment";
    }
}
