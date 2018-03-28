package io.lastwill.eventscan.events;

import io.lastwill.eventscan.model.CryptoCurrency;
import io.mywish.scanner.model.BaseEvent;
import io.mywish.scanner.model.NetworkType;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.web3j.protocol.core.methods.response.Transaction;

import java.math.BigInteger;

@Getter
public abstract class PaymentEvent extends BaseEvent {
    private final Transaction transaction;
    private final String address;
    private final BigInteger amount;
    private final CryptoCurrency currency;
    private final boolean isSuccess;

    public PaymentEvent(NetworkType networkType, Transaction transaction, String address, BigInteger amount, CryptoCurrency currency, boolean isSuccess) {
        super(networkType);
        this.transaction = transaction;
        this.address = address;
        this.amount = amount;
        this.currency = currency;
        this.isSuccess = isSuccess;
    }
}
