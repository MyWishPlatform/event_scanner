package io.mywish.eos.blockchain.model;

import lombok.Getter;

import java.math.BigDecimal;
import java.math.BigInteger;

/**
 * Universal EOS structure to hold amount of tokens. It might be max supply, or just value.
 */
@Getter
public class MaxSupply {
    private final BigDecimal value;
    private final String symbol;
    private final int decimals;

    public MaxSupply(BigDecimal value, String symbol, int decimals) {
        this.value = value;
        this.symbol = symbol;
        this.decimals = decimals;
    }

    public BigInteger getIntegerValue() {
        return BigDecimal.TEN.pow(decimals).multiply(value).toBigInteger();
    }
}
