package io.lastwill.eventscan.messages;

import lombok.Getter;
import lombok.ToString;

@Getter
@ToString(callSuper = true)
public class WhitelistRemovedNotify extends NotifyContract {
    private final String address;

    public WhitelistRemovedNotify(int contractId, String txHash, String address) {
        super(contractId, PaymentStatus.COMMITTED, txHash);
        this.address = address;
    }

    @Override
    public String getType() {
        return "whitelistRemoved";
    }
}
