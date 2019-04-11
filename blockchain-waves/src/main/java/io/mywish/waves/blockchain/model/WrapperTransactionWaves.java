package io.mywish.waves.blockchain.model;

import com.wavesplatform.wavesj.Transaction;
import io.mywish.blockchain.WrapperOutput;
import io.mywish.blockchain.WrapperTransaction;
import lombok.Getter;

import java.util.List;

@Getter
public class WrapperTransactionWaves extends WrapperTransaction {
    private final Transaction transaction;

    public WrapperTransactionWaves(String hash, List<String> inputs, List<WrapperOutput> outputs, boolean contractCreation, Transaction transaction) {
        super(hash, inputs, outputs, contractCreation);
        this.transaction = transaction;
    }
}
