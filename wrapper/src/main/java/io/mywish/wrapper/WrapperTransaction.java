package io.mywish.wrapper;

import lombok.Getter;
import lombok.Setter;

import javax.annotation.Nullable;
import java.util.List;

@Getter
public class WrapperTransaction {
    private final String hash;
    private final List<String> inputs;
    private final List<WrapperOutput> outputs;
    private final boolean contractCreation;
    @Setter
    private String creates = null;

    public WrapperTransaction(
            final String txHash,
            final List<String> inputs,
            final List<WrapperOutput> outputs,
            boolean contractCreation) {
        this.hash = txHash;
        this.inputs = inputs;
        this.outputs = outputs;
        this.contractCreation = contractCreation;
    }
}
