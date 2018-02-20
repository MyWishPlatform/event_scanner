package io.lastwill.eventscan.events;

import io.lastwill.eventscan.model.CryptoCurrency;
import io.lastwill.eventscan.model.UserProfile;
import lombok.Getter;
import org.web3j.protocol.core.methods.response.Transaction;

import java.math.BigInteger;

@Getter
public class UserPaymentEvent extends PaymentEvent {
    private final UserProfile userProfile;

    public UserPaymentEvent(Transaction transaction, BigInteger amount, CryptoCurrency currency, boolean isSuccess, UserProfile userProfile) {
        super(transaction, userProfile.getInternalAddress(), amount, currency, isSuccess);
        this.userProfile = userProfile;
    }
}
