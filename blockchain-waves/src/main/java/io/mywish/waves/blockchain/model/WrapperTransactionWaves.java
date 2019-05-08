package io.mywish.waves.blockchain.model;

import io.mywish.blockchain.WrapperOutput;
import io.mywish.blockchain.WrapperTransaction;
import lombok.Getter;

import java.util.List;

@Getter
public class WrapperTransactionWaves extends WrapperTransaction {
    public WrapperTransactionWaves(String hash, List<String> inputs, List<WrapperOutput> outputs, boolean contractCreation) {
        super(hash, inputs, outputs, contractCreation);
    }
}
