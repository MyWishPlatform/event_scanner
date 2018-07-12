package io.lastwill.eventscan.messages;

import lombok.Getter;

import java.math.BigInteger;

@Getter
public class TokensSentNotify extends NotifyContract {
    private final String investorAddress;
    private final BigInteger amount;

    public TokensSentNotify(int contractId, String txHash, String investorAddress, BigInteger amount) {
        super(contractId, PaymentStatus.COMMITTED, txHash);
        this.investorAddress = investorAddress;
        this.amount = amount;
    }

    @Override
    public String getType() {
        return "tokensSent";
    }
}
