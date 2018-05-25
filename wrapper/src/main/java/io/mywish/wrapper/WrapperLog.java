package io.mywish.wrapper;

import lombok.Getter;
import org.web3j.abi.datatypes.Type;

import java.util.List;

@Getter
public class WrapperLog {
    private final String address;
    private final String name;
    protected final List<Object> args;

    public WrapperLog(String address, String name, List<Object> args) {
        this.address = address;
        this.name = name;
        this.args = args;
    }
}
