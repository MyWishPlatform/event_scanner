package io.lastwill.eventscan.events;

import io.lastwill.eventscan.model.BaseEvent;
import io.lastwill.eventscan.model.CryptoCurrency;
import io.mywish.wrapper.WrapperTransaction;
import io.lastwill.eventscan.model.NetworkType;
import lombok.Getter;
import java.math.BigInteger;

@Getter
public abstract class PaymentEvent extends BaseEvent {
    private final WrapperTransaction transaction;
    private final String address;
    private final BigInteger amount;
    private final CryptoCurrency currency;
    private final boolean isSuccess;

    public PaymentEvent(NetworkType networkType, WrapperTransaction transaction, String address, BigInteger amount, CryptoCurrency currency, boolean isSuccess) {
        super(networkType);
        this.transaction = transaction;
        this.address = address;
        this.amount = amount;
        this.currency = currency;
        this.isSuccess = isSuccess;
    }
}
