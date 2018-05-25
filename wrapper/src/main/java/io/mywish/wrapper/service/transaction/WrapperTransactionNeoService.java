package io.mywish.wrapper.service.transaction;

import io.mywish.neocli4j.Transaction;
import io.mywish.neocli4j.TransactionInput;
import io.mywish.wrapper.WrapperOutput;
import io.mywish.wrapper.WrapperTransaction;
import io.mywish.wrapper.service.WrapperTransactionService;
import io.mywish.wrapper.service.output.WrapperOutputNeoService;
import io.mywish.wrapper.transaction.WrapperTransactionNeo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class WrapperTransactionNeoService implements WrapperTransactionService<Transaction> {
    @Autowired
    private WrapperOutputNeoService outputBuilder;

    @Override
    public WrapperTransaction build(Transaction transaction) {
        String hash = transaction.getHash();
        List<String> inputs = transaction.getInputs().stream().map(TransactionInput::getAddress).collect(Collectors.toList());
        List<WrapperOutput> outputs = transaction.getOutputs().stream().map(output -> outputBuilder.build(transaction, output)).collect(Collectors.toList());
        boolean contractCreation = transaction.getContracts().size() == 0 && transaction.getType() == Transaction.Type.InvocationTransaction;
        WrapperTransaction res = new WrapperTransactionNeo(
                hash,
                inputs,
                outputs,
                contractCreation,
                transaction.getType(),
                transaction.getContracts()
        );
        if (res.isContractCreation()) res.setCreates(transaction.getContracts().get(0));
        return res;
    }
}
