package io.mywish.joule.dto;

import lombok.Getter;

import java.math.BigInteger;

@Getter
public class SignTransaction {
    private final String source;
    private final String dest;
    private final BigInteger value;
    private final BigInteger gaslimit;
    private final String data;
    private final BigInteger nonce;

    public SignTransaction(String source, String dest, BigInteger value, BigInteger gaslimit, String data, BigInteger nonce) {
        this.source = source;
        this.dest = dest;
        this.value = value;
        this.gaslimit = gaslimit;
        this.data = data;
        this.nonce = nonce;
    }
}
