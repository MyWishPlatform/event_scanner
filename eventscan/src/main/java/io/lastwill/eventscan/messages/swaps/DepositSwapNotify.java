package io.lastwill.eventscan.messages.swaps;

import io.lastwill.eventscan.events.model.contract.swaps.DepositSwapEvent;
import io.lastwill.eventscan.messages.BaseNotify;
import io.lastwill.eventscan.messages.PaymentStatus;
import lombok.Getter;

import java.math.BigInteger;

@Getter
public class DepositSwapNotify extends BaseNotify {
    private final String token;
    private final String userAddress;
    private final BigInteger amount;
    private final BigInteger balance;

    public DepositSwapNotify(PaymentStatus status, String transactionHash, DepositSwapEvent event) {
        super(status, transactionHash);
        this.token = event.getToken();
        this.userAddress = event.getUser();
        this.amount = event.getAmount();
        this.balance = event.getBalance();
    }

    @Override
    public String getType() {
        return "depositSwaps";
    }
}