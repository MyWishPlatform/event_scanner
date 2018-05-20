package io.lastwill.eventscan.events;

import io.lastwill.eventscan.model.CryptoCurrency;
import io.lastwill.eventscan.model.Product;
import io.lastwill.eventscan.model.NetworkType;
import io.mywish.wrapper.WrapperTransaction;
import lombok.Getter;
import org.bitcoinj.core.TransactionOutput;

import java.math.BigInteger;

@Getter
public class ProductPaymentEvent extends PaymentEvent {
    private final Product product;
    private final TransactionOutput transactionOutput;

    public ProductPaymentEvent(NetworkType networkType, WrapperTransaction transaction, String address, BigInteger amount, CryptoCurrency currency, boolean isSuccess, Product product, TransactionOutput transactionOutput) {
        super(networkType, transaction, address, amount, currency, isSuccess);
        this.product = product;
        this.transactionOutput = transactionOutput;
    }
}
