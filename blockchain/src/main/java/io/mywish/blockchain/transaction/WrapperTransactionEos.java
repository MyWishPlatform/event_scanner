package io.mywish.blockchain.transaction;

import io.mywish.eoscli4j.model.TransactionStatus;
import io.mywish.blockchain.WrapperOutput;
import io.mywish.blockchain.WrapperTransaction;
import lombok.Getter;

import java.util.List;

@Getter
public class WrapperTransactionEos extends WrapperTransaction {
    private final TransactionStatus status;

    public WrapperTransactionEos(String hash, List<String> inputs, List<WrapperOutput> outputs, boolean contractCreation, TransactionStatus status) {
        super(hash, inputs, outputs, contractCreation);
        this.status = status;
    }
}
