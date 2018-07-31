package io.mywish.eoscli4j.model.response;

import lombok.Getter;

import java.math.BigInteger;

@Getter
public class BalanceResponse extends Response {
    private final BigInteger value;
    private final int decimals;

    public BalanceResponse(BigInteger value, int decimals) {
        this.value = value;
        this.decimals = decimals;
    }
}
