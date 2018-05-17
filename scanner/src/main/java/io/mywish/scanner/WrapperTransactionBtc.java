package io.mywish.scanner;

import org.bitcoinj.core.NetworkParameters;
import org.bitcoinj.core.Transaction;

import java.math.BigInteger;
import java.util.stream.Collectors;

public class WrapperTransactionBtc extends WrapperTransaction {
    public WrapperTransactionBtc(Transaction transaction, NetworkParameters networkParameters) {
        super(
                transaction.getHashAsString(),
                null,
                transaction.getOutputs().stream().map(output -> new WrapperOutput(output.getScriptPubKey().getToAddress(networkParameters, true).toBase58(), BigInteger.valueOf(output.getValue().getValue()))).collect(Collectors.toList()),
                false
        );
    }
}
