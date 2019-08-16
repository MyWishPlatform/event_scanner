package io.lastwill.eventscan.messages.swaps2;

import io.lastwill.eventscan.events.model.contract.swaps2.DepositEvent;
import lombok.Getter;

import java.math.BigInteger;

@Getter
public class DepositNotify extends Swaps2Notify {

    private final String token;
    private final String user;
    private final BigInteger amount;
    private final BigInteger balance;

    public DepositNotify(String txHash,
                         boolean isSuccess,
                         String address,
                         DepositEvent event
    ) {
        super(txHash, isSuccess, address, event.getId());
        this.token = event.getToken();
        this.user = event.getUser();
        this.amount = event.getAmount();
        this.balance = event.getBalance();
    }

    @Override
    public String getType() {
        return "depositOrder";
    }
}
