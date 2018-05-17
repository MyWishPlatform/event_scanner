package io.mywish.scanner;

import com.glowstick.neocli4j.Transaction;
import com.glowstick.neocli4j.TransactionInput;
import lombok.Getter;

import java.util.stream.Collectors;

@Getter
public class WrapperTransactionNeo extends WrapperTransaction {
    private final Transaction.Type type;

    public WrapperTransactionNeo(Transaction transaction) {
        super(
                transaction.getHash(),
                transaction.getInputs().stream().map(TransactionInput::getAddress).collect(Collectors.toList()),
                transaction.getOutputs().stream().map(output -> new WrapperOutput(output.getAddress(), output.getValue())).collect(Collectors.toList()),
                transaction.getContracts().size() == 0 && transaction.getType() == Transaction.Type.InvocationTransaction
        );
        this.type = transaction.getType();
    }
}
