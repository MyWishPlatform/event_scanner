package io.lastwill.eventscan.messages;

import lombok.Getter;

@Getter
public class ContractDeployed extends NotifyContract {
    private final String address;
    private final String transactionHash;

    public ContractDeployed(int contractId, PaymentStatus state, String address, String transactionHash) {
        super(contractId, state);
        this.address = address;
        this.transactionHash = transactionHash;
    }

    @Override
    public String getType() {
        return "deployed";
    }
}
