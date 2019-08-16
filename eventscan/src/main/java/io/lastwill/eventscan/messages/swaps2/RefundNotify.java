package io.lastwill.eventscan.messages.swaps2;

import io.lastwill.eventscan.events.model.contract.swaps2.RefundEvent;
import lombok.Getter;
import lombok.ToString;

import java.math.BigInteger;

@Getter
@ToString(callSuper = true)
public class RefundNotify extends Swaps2Notify {
    private final String token;
    private final String user;
    private final BigInteger amount;

    public RefundNotify(String txHash,
                         boolean isSuccess,
                         String address,
                         RefundEvent event
    ) {
        super(txHash, isSuccess, address, event.getId());
        this.token = event.getToken();
        this.user = event.getUser();
        this.amount = event.getAmount();
    }

    @Override
    public String getType() {
        return "refundOrder";
    }
}
