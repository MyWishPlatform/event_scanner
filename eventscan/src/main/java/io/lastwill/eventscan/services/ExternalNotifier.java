package io.lastwill.eventscan.services;

import io.lastwill.eventscan.messages.BaseNotify;
import io.lastwill.eventscan.messages.PaymentStatus;
import io.lastwill.eventscan.model.AddressLock;
import io.lastwill.eventscan.model.Contract;
import io.lastwill.eventscan.model.CryptoCurrency;
import io.lastwill.eventscan.model.UserProfile;
import io.mywish.scanner.model.NetworkType;

import java.math.BigInteger;

public interface ExternalNotifier {
    void send(final NetworkType networkType, final BaseNotify notify);
}
