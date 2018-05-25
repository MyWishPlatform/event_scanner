package io.mywish.wrapper.service.output;

import io.mywish.wrapper.WrapperOutput;
import org.bitcoinj.core.NetworkParameters;
import org.bitcoinj.core.Transaction;
import org.bitcoinj.core.TransactionOutput;
import org.springframework.stereotype.Component;

import java.math.BigInteger;

@Component
public class WrapperOutputBtcService {
    public WrapperOutput build(Transaction transaction, TransactionOutput output, NetworkParameters networkParameters) {
        return new WrapperOutput(
                transaction.getHashAsString(),
                output.getIndex(),
                output.getScriptPubKey().getToAddress(networkParameters, true).toBase58(),
                BigInteger.valueOf(output.getValue().getValue())
        );
    }
}
