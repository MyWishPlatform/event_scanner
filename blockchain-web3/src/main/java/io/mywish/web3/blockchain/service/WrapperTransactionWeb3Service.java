package io.mywish.web3.blockchain.service;

import io.mywish.blockchain.WrapperOutput;
import io.mywish.blockchain.WrapperTransaction;
import io.mywish.blockchain.service.WrapperTransactionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.web3j.protocol.core.methods.response.Transaction;

import java.util.Collections;
import java.util.List;

@Component
public class WrapperTransactionWeb3Service implements WrapperTransactionService<Transaction> {
    private final static String ZERO_ADDRESS = "0x0000000000000000000000000000000000000000";

    @Autowired
    private WrapperOutputWeb3Service outputBuilder;

    @Override
    public WrapperTransaction build(Transaction transaction) {
        String hash = transaction.getHash();
        List<String> inputs = Collections.singletonList(transaction.getFrom());
        List<WrapperOutput> outputs = Collections.singletonList(outputBuilder.build(transaction));
        boolean contractCreation = transaction.getTo() == null || ZERO_ADDRESS.equalsIgnoreCase(transaction.getTo());
        WrapperTransaction wrapper = new WrapperTransaction(
                hash,
                inputs,
                outputs,
                contractCreation
        );
        wrapper.setCreates(transaction.getCreates());
        return wrapper;
    }
}
