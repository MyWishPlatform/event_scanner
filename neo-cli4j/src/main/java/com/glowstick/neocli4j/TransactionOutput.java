package com.glowstick.neocli4j;

public class TransactionOutput {
    private final String address;
    private final String asset;
    private final Double value;

    public TransactionOutput(String address, String asset, Double value) {
        this.address = address;
        this.asset = asset;
        this.value = value;
    }

    public final String getAddress() {
        return this.address;
    }

    public final String getAsset() {
        return this.asset;
    }

    public final double getValue() {
        return this.value;
    }
}
