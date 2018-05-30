package io.mywish.wrapper;

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

    public WrapperTransaction(String txHash, List<String> inputs, List<WrapperOutput> outputs, boolean contractCreation) {
        this.hash = txHash;
        this.inputs = inputs;
        this.outputs = outputs;
        this.contractCreation = contractCreation;
    }
}
