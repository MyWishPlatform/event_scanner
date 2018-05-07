package com.glowstick.neocli4j;

import java.util.List;

public class Transaction {
    public enum Type {
        Miner,
        Issue,
        Claim,
        Enrollment,
        Register,
        Contract,
        Agency,
        Publish,
        Invocation
    }

    private Type type;
    private final String hash;
    private final List<TransactionOutput> outputs;

    public Transaction(Type type, String hash, List<TransactionOutput> outputs) {
        this.type = type;
        this.hash = hash;
        this.outputs = outputs;
    }

    public String getHash() {
        return this.hash;
    }

    public List<TransactionOutput> getOutputs() {
        return this.outputs;
    }

    public Type getType() {
        return this.type;
    }
}
