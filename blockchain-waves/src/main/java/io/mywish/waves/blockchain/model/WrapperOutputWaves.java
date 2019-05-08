package io.mywish.waves.blockchain.model;

import com.fasterxml.jackson.databind.JsonNode;
import io.mywish.blockchain.WrapperOutput;
import lombok.Getter;

import java.math.BigInteger;

@Getter
public class WrapperOutputWaves extends WrapperOutput {
    private final JsonNode transaction;

    public WrapperOutputWaves(String parentTransaction, String address, BigInteger value, JsonNode transaction) {
        super(parentTransaction, 0, address, value, new byte[0]);
        this.transaction = transaction;
    }
}
