package io.mywish.wrapper.model.output;

import com.fasterxml.jackson.databind.JsonNode;
import io.mywish.wrapper.WrapperOutput;
import lombok.Getter;

import java.math.BigInteger;

@Getter
public class WrapperOutputEos extends WrapperOutput {
    private final String name;
    private final JsonNode actionArguments;

    public WrapperOutputEos(String parentTransaction, String account, String name, JsonNode actionArguments) {
        super(parentTransaction, 0, account, BigInteger.ZERO, new byte[0]);
        this.name = name;
        this.actionArguments = actionArguments;
    }
}
