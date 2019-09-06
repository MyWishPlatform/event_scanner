package io.lastwill.eventscan.messages.swaps;

import io.lastwill.eventscan.events.model.contract.swaps.DepositSwapEvent;
import io.lastwill.eventscan.messages.NotifyContract;
import io.lastwill.eventscan.messages.PaymentStatus;
import lombok.Getter;

import java.math.BigInteger;

@Getter
public class DepositSwapNotify extends NotifyContract {
    private final String token;
    private final String userAddress;
    private final BigInteger amount;
    private final BigInteger balance;

    public DepositSwapNotify(int contractId, PaymentStatus status, String txHash, DepositSwapEvent event) {
        super(contractId, status, txHash);
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