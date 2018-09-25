package io.mywish.blockchain;

import lombok.Getter;

import java.math.BigInteger;

@Getter
public class WrapperOutput {
    private final String parentTransaction;
    private final String address;
    private final Integer index;
    private final BigInteger value;
    private final byte[] rawOutputScript;

    public WrapperOutput(String parentTransaction, Integer index, String address, BigInteger value, byte[] rawOutputScript) {
        this.parentTransaction = parentTransaction;
        this.index = index;
        this.address = address;
        this.value = value;
        this.rawOutputScript = rawOutputScript;
    }
}
