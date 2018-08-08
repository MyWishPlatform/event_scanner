package io.mywish.wrapper.model.output;

import com.fasterxml.jackson.databind.JsonNode;
import io.mywish.wrapper.WrapperOutput;
import lombok.Getter;

import java.math.BigInteger;

@Getter
public class WrapperOutputEos extends WrapperOutput {
    private final String account;
    private final String name;
    private final JsonNode actionArguments;

    public WrapperOutputEos(String parentTransaction, String address, String account, String name, JsonNode actionArguments) {
        super(parentTransaction, 0, address, BigInteger.ZERO, new byte[0]);
        this.account = account;
        this.name = name;
        this.actionArguments = actionArguments;
    }
}
