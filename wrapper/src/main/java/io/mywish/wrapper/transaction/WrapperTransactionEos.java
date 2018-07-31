package io.mywish.wrapper.transaction;

import io.mywish.eoscli4j.model.Transaction;
import io.mywish.wrapper.WrapperOutput;
import io.mywish.wrapper.WrapperTransaction;
import lombok.Getter;

import java.util.List;

public class WrapperTransactionEos extends WrapperTransaction {
    @Getter
    private Transaction nativeTransaction;

    public WrapperTransactionEos(String hash, List<String> inputs, List<WrapperOutput> outputs, boolean contractCreation, Transaction nativeTransaction) {
        super(hash, inputs, outputs, contractCreation);
        this.nativeTransaction = nativeTransaction;
    }
}
