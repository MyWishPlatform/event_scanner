package io.lastwill.eventscan.messages;

import io.lastwill.eventscan.model.CryptoCurrency;
import io.lastwill.eventscan.model.Product;
import lombok.Getter;
import lombok.ToString;

import java.math.BigInteger;

@ToString(callSuper = true)
@Getter
public class MainPaymentNotify extends PaymentNotify {
    private final int outputIndex;

    public MainPaymentNotify(BigInteger amount, PaymentStatus status, String txHash, int outputIndex) {
        super(0, amount, status, txHash, CryptoCurrency.BTC, true);
        this.outputIndex = outputIndex;
    }

    @Override
    public String getType() {
        return "mainPayment";
    }

}
