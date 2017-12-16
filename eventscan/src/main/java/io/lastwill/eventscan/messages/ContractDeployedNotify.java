package io.lastwill.eventscan.messages;

import lombok.Getter;
import lombok.ToString;

@ToString(callSuper = true)
@Getter
public class ContractDeployedNotify extends NotifyContract {
    private final String address;
    private final String transactionHash;
    private final boolean status;

    public ContractDeployedNotify(int contractId, PaymentStatus state, String address, String transactionHash, boolean status) {
        super(contractId, state);
        this.address = address;
        this.transactionHash = transactionHash;
        this.status = status;
    }

    @Override
    public String getType() {
        return "deployed";
    }
}
