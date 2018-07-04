package io.mywish.wrapper.service.transaction;

import io.mywish.wrapper.WrapperOutput;
import io.mywish.wrapper.WrapperTransaction;
import io.mywish.wrapper.service.output.WrapperOutputBtcService;
import org.bitcoinj.core.NetworkParameters;
import org.bitcoinj.core.Transaction;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Component
public class WrapperTransactionBtcService {
    @Autowired
    private WrapperOutputBtcService outputBuilder;

    public WrapperTransaction build(Transaction transaction, NetworkParameters networkParameters) {
        String hash = transaction.getHashAsString();
        List<String> inputs = new ArrayList<>();
        List<WrapperOutput> outputs = transaction.getOutputs().stream()
                .map(output -> outputBuilder.build(transaction, output, networkParameters))
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
        return new WrapperTransaction(
                hash,
                inputs,
                outputs,
                false
        );
    }
}
