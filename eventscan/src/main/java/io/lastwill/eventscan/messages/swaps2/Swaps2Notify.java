package io.lastwill.eventscan.messages.swaps2;

import io.lastwill.eventscan.messages.BaseNotify;
import io.lastwill.eventscan.messages.PaymentStatus;
import lombok.Getter;
import lombok.ToString;

@ToString(callSuper = true)
@Getter
public abstract class Swaps2Notify extends BaseNotify {
    private final boolean isSuccess;
    private final String address;
    private final String id;

    public Swaps2Notify(String txHash, boolean isSuccess, String address, String id) {
        super(PaymentStatus.COMMITTED, txHash);
        this.isSuccess = isSuccess;
        this.address = address;
        this.id = id;
    }
}
