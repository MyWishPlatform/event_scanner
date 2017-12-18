package io.lastwill.eventscan.services;

import io.lastwill.eventscan.messages.PaymentStatus;
import io.lastwill.eventscan.model.Contract;
import io.lastwill.eventscan.model.Product;

import java.math.BigInteger;

public interface ExternalNotifier {
    void sendPaymentNotify(Product product, BigInteger balance, PaymentStatus status);

    void sendCheckRepeatNotify(Contract contract);

    void sendCheckedNotify(Contract contract, String transactionHash);

    void sendDeployedNotification(Contract contract, String address, String transactionHash, boolean committed, boolean status);

    void sendKilledNotification(Contract contract);

    void sendTriggeredNotification(Contract contract);
}
