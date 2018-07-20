package io.lastwill.eventscan.messages;

import lombok.Getter;

import java.math.BigInteger;

@Getter
public class TokensAddedNotify extends NotifyContract {
    private final String tokenAddress;
    private final BigInteger value;

    public TokensAddedNotify(int contractId, String txHash, String tokenAddress, BigInteger value) {
        super(contractId, PaymentStatus.COMMITTED, txHash);
        this.tokenAddress = tokenAddress;
        this.value = value;
    }

    @Override
    public String getType() {
        return "tokensAdded";
    }
}
