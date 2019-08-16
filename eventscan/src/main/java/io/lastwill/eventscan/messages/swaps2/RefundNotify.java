package io.lastwill.eventscan.messages.swaps2;

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
                         String id,
                         String token,
                         String user,
                         BigInteger amount
    ) {
        super(txHash, isSuccess, address, id);
        this.token = token;
        this.user = user;
        this.amount = amount;
    }

    @Override
    public String getType() {
        return "refundOrder";
    }
}
