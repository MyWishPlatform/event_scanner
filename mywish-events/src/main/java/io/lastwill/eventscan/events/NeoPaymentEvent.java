package io.lastwill.eventscan.events;

import io.lastwill.eventscan.model.CryptoCurrency;
import io.mywish.scanner.WrapperTransaction;
import io.mywish.scanner.model.NetworkType;
import lombok.Getter;

import java.math.BigInteger;

@Getter
public class NeoPaymentEvent extends PaymentEvent {
    private WrapperTransaction neoTransaction;
    public NeoPaymentEvent(NetworkType networkType, WrapperTransaction transaction, String address, BigInteger amount, boolean isSuccess) {
        super(networkType, null, address, amount, CryptoCurrency.NEO, isSuccess);
        this.neoTransaction = transaction;
    }
}
