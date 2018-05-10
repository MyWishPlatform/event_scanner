package io.lastwill.eventscan.events;

import io.lastwill.eventscan.model.CryptoCurrency;
import io.mywish.scanner.model.NetworkType;
import lombok.Getter;

import java.math.BigInteger;

@Getter
public class NeoPaymentEvent extends PaymentEvent {
    public NeoPaymentEvent(NetworkType networkType, String address, BigInteger amount, CryptoCurrency currency, boolean isSuccess) {
        super(networkType, null, address, amount, currency, isSuccess);
    }
}
