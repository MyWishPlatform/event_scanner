package io.lastwill.eventscan.services;

import io.lastwill.eventscan.messages.PaymentStatus;
import io.lastwill.eventscan.model.Contract;

import java.math.BigInteger;

public interface ExternalNotifier {
    void sendPaymentNotify(Contract contract, BigInteger balance, PaymentStatus status);

    void sendCheckRepeatNotify(Contract contract);

    void sendDeployedNotification(Contract contract, String address, String transactionHash, boolean committed);

    void sendKilledNotification(Contract contract);
}
