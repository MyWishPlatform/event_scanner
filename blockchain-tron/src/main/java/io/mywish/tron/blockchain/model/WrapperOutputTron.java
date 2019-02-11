package io.mywish.tron.blockchain.model;

import com.fasterxml.jackson.databind.JsonNode;
import io.mywish.blockchain.WrapperOutput;
import lombok.Getter;

import java.math.BigInteger;

@Getter
public class WrapperOutputTron extends WrapperOutput {
    private final JsonNode contract;

    public WrapperOutputTron(String parentTransaction, String address, BigInteger value, JsonNode contract) {
        super(parentTransaction, 0, address, value, new byte[0]);
        this.contract = contract;
    }
}
