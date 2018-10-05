package io.mywish.web3.blockchain.service;

import io.mywish.blockchain.WrapperOutput;
import io.mywish.blockchain.service.WrapperOutputService;
import org.springframework.stereotype.Component;
import org.web3j.protocol.core.methods.response.Transaction;

import javax.xml.bind.DatatypeConverter;
import java.math.BigInteger;

@Component
public class WrapperOutputWeb3Service implements WrapperOutputService<Transaction> {
    @Override
    public WrapperOutput build(Transaction transaction) {
        return new WrapperOutput(
                transaction.getHash(),
                0,
                transaction.getTo(),
                "0".equals(transaction.getValueRaw()) ? BigInteger.ZERO : transaction.getValue(),
                DatatypeConverter.parseHexBinary(transaction.getInput().substring(2))
        );
    }
}
