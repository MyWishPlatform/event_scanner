package io.lastwill.eventscan.events;

import com.glowstick.neocli4j.Transaction;
import io.lastwill.eventscan.model.CryptoCurrency;
import io.mywish.scanner.model.NetworkType;
import lombok.Getter;

import java.math.BigInteger;

@Getter
public class NeoPaymentEvent extends PaymentEvent {
    private Transaction neoTransaction;
    public NeoPaymentEvent(NetworkType networkType, Transaction transaction, String address, BigInteger amount, CryptoCurrency currency, boolean isSuccess) {
        super(networkType, null, address, amount, currency, isSuccess);
        this.neoTransaction = transaction;
    }
}
