package io.mywish.scanner;

import lombok.Getter;

import java.math.BigInteger;

@Getter
public class WrapperOutput {
    private String address;
    private BigInteger value;

    public WrapperOutput(String address, BigInteger value) {
        this.address = address;
        this.value = value;
    }
}
