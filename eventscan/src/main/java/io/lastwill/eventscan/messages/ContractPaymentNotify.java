package io.lastwill.eventscan.messages;

import io.lastwill.eventscan.model.CryptoCurrency;
import io.lastwill.eventscan.model.Product;
import lombok.Getter;
import lombok.ToString;

import java.math.BigInteger;

@ToString(callSuper = true)
@Getter
public class ContractPaymentNotify extends PaymentNotify {
    private final int contractId;

    public ContractPaymentNotify(BigInteger amount, PaymentStatus status, String txHash, Product product) {
        super(product.getUserId(), amount, status, txHash, CryptoCurrency.BTC, true);
        this.contractId = product.getId();
    }

    @Override
    public String getType() {
        return "contractPayment";
    }

}
