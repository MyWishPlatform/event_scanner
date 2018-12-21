package io.mywish.tron.blockchain.model;

import io.mywish.blockchain.WrapperOutput;
import io.mywish.blockchain.WrapperTransaction;
import io.mywish.troncli4j.model.TransactionStatus;
import lombok.Getter;

import java.util.List;

@Getter
public class WrapperTransactionTron extends WrapperTransaction {
    private final TransactionStatus status;

    public WrapperTransactionTron(String hash, List<String> inputs, List<WrapperOutput> outputs, boolean contractCreation, TransactionStatus status) {
        super(hash, inputs, outputs, contractCreation);
        this.status = status;
    }
}
