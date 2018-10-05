package io.lastwill.eventscan.events.model;

import io.lastwill.eventscan.model.CryptoCurrency;
import io.lastwill.eventscan.model.Product;
import io.lastwill.eventscan.model.NetworkType;
import io.mywish.blockchain.WrapperOutput;
import io.mywish.blockchain.WrapperTransaction;
import lombok.Getter;

import java.math.BigInteger;

@Getter
public class ProductPaymentEvent extends PaymentEvent {
    private final Product product;
    private final WrapperOutput transactionOutput;

    public ProductPaymentEvent(NetworkType networkType, WrapperTransaction transaction, String address, BigInteger amount, CryptoCurrency currency, boolean isSuccess, Product product, WrapperOutput transactionOutput) {
        super(networkType, transaction, address, amount, currency, isSuccess);
        this.product = product;
        this.transactionOutput = transactionOutput;
    }
}
