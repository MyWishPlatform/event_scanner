package io.lastwill.eventscan.messages.swaps;

import io.lastwill.eventscan.events.model.contract.swaps.RefundSwapEvent;
import io.lastwill.eventscan.messages.BaseNotify;
import io.lastwill.eventscan.messages.PaymentStatus;
import lombok.Getter;

import java.math.BigInteger;

@Getter
public class RefundSwapNotify extends BaseNotify {
    private final String token;
    private final String userAddress;
    private final BigInteger amount;

    public RefundSwapNotify(PaymentStatus status, String transactionHash, RefundSwapEvent event) {
        super(status, transactionHash);
        this.token = event.getToken();
        this.userAddress = event.getUser();
        this.amount = event.getAmount();
    }

    @Override
    public String getType() {
        return null;
    }
}