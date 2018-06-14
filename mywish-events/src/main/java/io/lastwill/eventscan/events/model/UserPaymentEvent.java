package io.lastwill.eventscan.events.model;

import io.lastwill.eventscan.model.CryptoCurrency;
import io.lastwill.eventscan.model.UserProfile;
import io.mywish.wrapper.WrapperTransaction;
import io.lastwill.eventscan.model.NetworkType;
import lombok.Getter;
import java.math.BigInteger;

@Getter
public class UserPaymentEvent extends PaymentEvent {
    private final UserProfile userProfile;

    public UserPaymentEvent(NetworkType networkType, WrapperTransaction transaction, BigInteger amount, CryptoCurrency currency, boolean isSuccess, UserProfile userProfile) {
        super(networkType, transaction, userProfile.getInternalAddress(), amount, currency, isSuccess);
        this.userProfile = userProfile;
    }
}
