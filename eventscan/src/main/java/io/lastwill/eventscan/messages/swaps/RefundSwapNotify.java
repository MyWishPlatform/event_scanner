package io.lastwill.eventscan.messages.swaps;

import io.lastwill.eventscan.events.model.contract.swaps.RefundSwapEvent;
import io.lastwill.eventscan.messages.NotifyContract;
import io.lastwill.eventscan.messages.PaymentStatus;
import lombok.Getter;

import java.math.BigInteger;

@Getter
public class RefundSwapNotify extends NotifyContract {
    private final String token;
    private final String userAddress;
    private final BigInteger amount;

    public RefundSwapNotify(int contractId, PaymentStatus status, String txHash, RefundSwapEvent event) {
        super(contractId, status, txHash);
        this.token = event.getToken();
        this.userAddress = event.getUser();
        this.amount = event.getAmount();
    }

    @Override
    public String getType() {
        return "refundSwaps";
    }
}