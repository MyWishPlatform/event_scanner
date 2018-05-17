package com.glowstick.neocli4j;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;

import java.math.BigInteger;

@Getter
public class TransactionOutput {
    private String address;
    private String asset;
    private BigInteger value;
    @JsonProperty("value")
    private void setValue(Double value) {
        this.value = BigInteger.valueOf(value.longValue());
    }
}
