package com.glowstick.neocli4j;

import java.util.List;

public class Transaction {
    private final String hash;
    private final List<TransactionOutput> outputs;

    public Transaction(String hash, List<TransactionOutput> outputs) {
        this.hash = hash;
        this.outputs = outputs;
    }

    public String getHash() {
        return this.hash;
    }

    public List<TransactionOutput> getOutputs() {
        return this.outputs;
    }
}
