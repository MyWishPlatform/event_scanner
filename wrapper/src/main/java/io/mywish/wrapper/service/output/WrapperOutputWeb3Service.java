package io.mywish.wrapper.service.output;

import io.mywish.wrapper.WrapperOutput;
import io.mywish.wrapper.service.WrapperOutputService;
import org.springframework.stereotype.Component;
import org.web3j.protocol.core.methods.response.Transaction;

import java.math.BigInteger;

@Component
public class WrapperOutputWeb3Service implements WrapperOutputService<Transaction> {
    @Override
    public WrapperOutput build(Transaction transaction) {
        return new WrapperOutput(
                transaction.getHash(),
                0,
                transaction.getTo(),
                "0".equals(transaction.getValueRaw()) ? BigInteger.ZERO : transaction.getValue()
        );
    }
}
