package io.mywish.wrapper;

import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

@Getter
public class WrapperTransactionReceipt {
    private final String transactionHash;
    private final List<String> contracts;
    private final List<ContractEvent> logs;
    private final boolean success;

    public WrapperTransactionReceipt(String txHash, List<String> contracts, List<ContractEvent> logs, boolean success) {
        this.transactionHash = txHash;
        this.contracts = contracts;
        this.logs = logs;
        this.success = success;
    }
}
