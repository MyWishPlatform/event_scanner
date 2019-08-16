package io.lastwill.eventscan.events.model;

import io.lastwill.eventscan.events.model.contract.swaps.RefundSwapEvent;
import io.lastwill.eventscan.model.NetworkType;
import io.mywish.blockchain.WrapperTransaction;
import lombok.Getter;

import java.math.BigInteger;

@Getter
public class SwapRefundEvent extends BaseEvent {
    private final String token;
    private final String userAddress;
    private final BigInteger amount;
    private final WrapperTransaction transaction;

    public SwapRefundEvent(
            NetworkType networkType,
            WrapperTransaction transaction,
            RefundSwapEvent event
    ) {
        super(networkType);
        this.transaction = transaction;
        this.token = event.getToken();
        this.userAddress = event.getUser();
        this.amount = event.getAmount();
    }
}
