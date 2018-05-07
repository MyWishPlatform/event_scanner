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

    private final Type type;
    private final String hash;
    private final List<TransactionOutput> outputs;
    private final String script;

    public Transaction(Type type, String hash, List<TransactionOutput> outputs, String script) {
        this.type = type;
        this.hash = hash;
        this.outputs = outputs;
        this.script = script;
    }

    public Type getType() {
        return this.type;
    }

    public String getHash() {
        return this.hash;
    }

    public List<TransactionOutput> getOutputs() {
        return this.outputs;
    }

    public String getScript() {
        return this.script;
    }
}
