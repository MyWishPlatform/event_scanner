package io.lastwill.eventscan.messages;

import lombok.Getter;
import lombok.ToString;

@ToString(callSuper = true)
@Getter
public class ContractDeployedNotify extends NotifyContract {
    private final String address;
    private final String transactionHash;

    public ContractDeployedNotify(int contractId, PaymentStatus state, String address, String transactionHash) {
        super(contractId, state);
        this.address = address;
        this.transactionHash = transactionHash;
    }

    @Override
    public String getType() {
        return "deployed";
    }
}
