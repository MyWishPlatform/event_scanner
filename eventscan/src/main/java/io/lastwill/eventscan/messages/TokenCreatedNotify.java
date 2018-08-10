package io.lastwill.eventscan.messages;

import lombok.Getter;

@Getter
public class TokenCreatedNotify extends NotifyContract {
    private final String address;

    public TokenCreatedNotify(int contractId, PaymentStatus status, String txHash, String address) {
        super(contractId, status, txHash);
        this.address = address;
    }

    @Override
    public String getType() {
        return "tokenCreated";
    }
}
