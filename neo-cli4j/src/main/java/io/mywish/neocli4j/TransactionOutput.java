package io.mywish.neocli4j;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;

import java.math.BigInteger;

@Getter
public class TransactionOutput {
    @JsonProperty("n")
    private Integer index;
    private String address;
    private String asset;
    private BigInteger value;
    @JsonProperty("value")
    private void setValue(Double value) {
        this.value = BigInteger.valueOf(value.longValue());
    }
}
