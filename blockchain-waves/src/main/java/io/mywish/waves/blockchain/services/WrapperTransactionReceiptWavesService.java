package io.mywish.waves.blockchain.services;

import io.mywish.blockchain.WrapperOutput;
import io.mywish.blockchain.WrapperTransactionReceipt;
import io.mywish.waves.blockchain.model.WrapperTransactionWaves;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class WrapperTransactionReceiptWavesService {
    public WrapperTransactionReceipt build(WrapperTransactionWaves transaction) {
        List<String> contracts = transaction
                .getOutputs()
                .stream()
                .map(WrapperOutput::getAddress)
                .collect(Collectors.toList());

        return new WrapperTransactionReceipt(
                transaction.getHash(),
                contracts,
                Collections.emptyList(),
                true
        );
    }
}
