package io.mywish.wrapper.service.transaction;

import io.mywish.eoscli4j.model.Transaction;
import io.mywish.wrapper.WrapperOutput;
import io.mywish.wrapper.WrapperTransaction;
import io.mywish.wrapper.service.WrapperTransactionService;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;

@Component
public class WrapperTransactionEosService implements WrapperTransactionService<Transaction> {
    @Override
    public WrapperTransaction build(Transaction transaction) {
        String hash = transaction.getTrx().getId();
        List<String> inputs = Collections.emptyList();
        List<WrapperOutput> outputs = Collections.emptyList();
        Boolean contractCreation = false; // TODO: implement
        return new WrapperTransaction(hash, inputs, outputs, contractCreation);
    }
}
