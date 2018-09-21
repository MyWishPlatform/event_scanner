package io.mywish.blockchain;

import lombok.Getter;
import lombok.Setter;

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

    public boolean isSingleOutput() {
        return outputs.size() == 1;
    }

    public String getSingleOutputAddress() {
        return outputs.get(0).getAddress();
    }

    public boolean isSingleInput() {
        return inputs.size() == 1;
    }

    public String getSingleInputAddress() {
        return inputs.get(0);
    }
}
