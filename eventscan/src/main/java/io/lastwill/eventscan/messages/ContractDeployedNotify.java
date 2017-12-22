package io.lastwill.eventscan.messages;

import lombok.Getter;
import lombok.ToString;

@ToString(callSuper = true)
@Getter
public class ContractDeployedNotify extends NotifyContract {
    private final String address;
    private final String transactionHash;
    private final boolean isSuccess;

    public ContractDeployedNotify(int contractId, PaymentStatus status, String address, String transactionHash, boolean isSuccess) {
        super(contractId, status, transactionHash);
        this.address = address;
        this.transactionHash = transactionHash;
        this.isSuccess = isSuccess;
    }

    @Override
    public String getType() {
        return "deployed";
    }
}
