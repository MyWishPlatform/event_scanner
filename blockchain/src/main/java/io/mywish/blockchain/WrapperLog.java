package io.mywish.blockchain;

import lombok.Getter;

import java.util.List;

@Getter
public class WrapperLog {
    private final String address;
    private final String name;
    private final String signature;
    protected final List<Object> args;

    public WrapperLog(String address, String name, String signature, List<Object> args) {
        this.address = address;
        this.name = name;
        this.signature = signature;
        this.args = args;
    }
}
