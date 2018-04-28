package com.glowstick.neocli4j;

public class TransactionOutput {
    private final String address;


    public TransactionOutput(String address) {
        this.address = address;
    }

    public String getAddress() {
        return this.address;
    }

    public Script getScriptPubKey() throws ScriptException {
        return null;
    }
}
