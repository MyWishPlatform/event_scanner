package io.mywish.wrapper;

import lombok.Getter;

import java.math.BigInteger;

@Getter
public class WrapperOutput {
    final private String parentTransaction;
    final private String address;
    final private Integer index;
    final private BigInteger value;

    public WrapperOutput(String parentTransaction, Integer index, String address, BigInteger value) {
        this.parentTransaction = parentTransaction;
        this.index = index;
        this.address = address;
        this.value = value;
    }
}
