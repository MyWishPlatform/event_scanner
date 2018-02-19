package io.lastwill.eventscan.services;

import io.lastwill.eventscan.messages.PaymentStatus;
import io.lastwill.eventscan.model.Contract;
import io.lastwill.eventscan.model.CryptoCurrency;
import io.lastwill.eventscan.model.UserProfile;

import java.math.BigInteger;

public interface ExternalNotifier {
    void sendPaymentNotify(UserProfile userProfile, BigInteger amount, PaymentStatus status, String txHash, CryptoCurrency currency, boolean isSuccess);

    void sendCheckRepeatNotify(Contract contract, String transactionHash);

    void sendCheckedNotify(Contract contract, String transactionHash);

    void sendDeployedNotification(Contract contract, String address, String transactionHash, boolean committed, boolean status);

    void sendKilledNotification(Contract contract, String transactionHash);

    void sendTriggeredNotification(Contract contract, String transactionHash);

    void sendOwnershipTransferredNotification(Contract contract, String transactionHash);

    void sendInitializedNotification(Contract contract, String transactionHash);

    void sendFinalizedNotification(Contract contract, String transactionHash);
}
