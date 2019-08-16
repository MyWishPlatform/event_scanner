package io.lastwill.eventscan.messages.swaps;

import io.lastwill.eventscan.events.model.contract.swaps.DepositEvent;
import io.lastwill.eventscan.messages.BaseNotify;
import io.lastwill.eventscan.messages.PaymentStatus;
import lombok.Getter;

import java.math.BigInteger;

@Getter
public class DepositNotify extends BaseNotify {
    private final String token;
    private final String userAddress;
    private final BigInteger amount;
    private final BigInteger balance;

    public DepositNotify(PaymentStatus status, String transactionHash, DepositEvent event) {
        super(status, transactionHash);
        this.token = event.getToken();
        this.userAddress = event.getUser();
        this.amount = event.getAmount();
        this.balance = event.getBalance();
    }

    @Override
    public String getType() {
        return null;
    }
}