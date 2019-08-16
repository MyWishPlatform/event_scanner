package io.lastwill.eventscan.messages.swaps2;

import java.math.BigInteger;

public class DepositNotify extends Swaps2Notify {

    private final String token;
    private final String user;
    private final BigInteger amount;
    private final BigInteger balance;

    public DepositNotify(String txHash,
                         boolean isSuccess,
                         String address,
                         String id,
                         String token,
                         String user,
                         BigInteger amount,
                         BigInteger balance
    ) {
        super(txHash, isSuccess, address, id);
        this.token = token;
        this.user = user;
        this.amount = amount;
        this.balance = balance;
    }

    @Override
    public String getType() {
        return "depositOrder";
    }
}
