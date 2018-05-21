package io.mywish.wrapper.transaction;

import com.glowstick.neocli4j.Transaction;
import com.glowstick.neocli4j.TransactionInput;
import io.mywish.wrapper.WrapperOutput;
import io.mywish.wrapper.WrapperTransaction;
import lombok.Getter;

import java.util.List;
import java.util.stream.Collectors;

@Getter
public class WrapperTransactionNeo extends WrapperTransaction {
    private final Transaction.Type type;
    private final List<String> contracts;

    public WrapperTransactionNeo(Transaction transaction) {
        super(
                transaction.getHash(),
                transaction.getInputs().stream().map(TransactionInput::getAddress).collect(Collectors.toList()),
                transaction.getOutputs().stream().map(output -> new WrapperOutput(transaction.getHash(), output.getIndex(), output.getAddress(), output.getValue())).collect(Collectors.toList()),
                transaction.getContracts().size() == 0 && transaction.getType() == Transaction.Type.InvocationTransaction
        );
        this.type = transaction.getType();
        this.contracts = transaction.getContracts();
    }
}
