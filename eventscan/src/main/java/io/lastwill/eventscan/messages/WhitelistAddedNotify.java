package io.lastwill.eventscan.messages;

import lombok.Getter;
import lombok.ToString;

@Getter
@ToString(callSuper = true)
public class WhitelistAddedNotify extends NotifyContract {
    private final String address;

    public WhitelistAddedNotify(int contractId, String txHash, String address) {
        super(contractId, PaymentStatus.COMMITTED, txHash);
        this.address = address;
    }

    @Override
    public String getType() {
        return "whitelistAdded";
    }
}
