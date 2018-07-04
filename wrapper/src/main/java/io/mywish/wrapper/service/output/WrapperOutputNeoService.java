package io.mywish.wrapper.service.output;

import io.mywish.neocli4j.Transaction;
import io.mywish.neocli4j.TransactionOutput;
import io.mywish.wrapper.WrapperOutput;
import org.springframework.stereotype.Component;

@Component
public class WrapperOutputNeoService {
    public WrapperOutput build(Transaction transaction, TransactionOutput output) {
        return new WrapperOutput(
                transaction.getHash(),
                output.getIndex(),
                output.getAddress(),
                output.getValue(),
                // TODO: add to neo raw output if needed
                new byte[0]);
    }
}
