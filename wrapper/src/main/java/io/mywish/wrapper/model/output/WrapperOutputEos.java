package io.mywish.wrapper.model.output;

import com.fasterxml.jackson.databind.JsonNode;
import io.mywish.wrapper.WrapperOutput;
import lombok.Getter;

import java.math.BigInteger;

@Getter
public class WrapperOutputEos extends WrapperOutput {
    private final JsonNode actionArguments;

    public WrapperOutputEos(String parentTransaction, Integer index, String address, BigInteger value, byte[] rawOutputScript, JsonNode actionArguments) {
        super(parentTransaction, index, address, value, rawOutputScript);
        this.actionArguments = actionArguments;
    }
}
