package io.lastwill.eventscan.messages;

import lombok.Getter;

@Getter
public class AccountCreatedNotify extends NotifyContract {
    /**
     * Alex asked me very gently to keep it address instead of name =)
     */
    private final String address;

    public AccountCreatedNotify(int contractId, PaymentStatus status, String txHash, String address) {
        super(contractId, status, txHash);
        this.address = address;
    }

    @Override
    public String getType() {
        return "newAccount";
    }
}
