package io.mywish.waves.blockchain.services;

import io.mywish.blockchain.ContractEvent;
import io.mywish.blockchain.WrapperOutput;
import io.mywish.blockchain.WrapperTransactionReceipt;
import io.mywish.waves.blockchain.model.WrapperOutputWaves;
import io.mywish.waves.blockchain.model.WrapperTransactionWaves;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class WrapperTransactionReceiptWavesService {
    @Autowired
    private WrapperLogWavesService logBuilder;

    public WrapperTransactionReceipt build(WrapperTransactionWaves transaction) {
        List<String> contracts = transaction
                .getOutputs()
                .stream()
                .map(WrapperOutput::getAddress)
                .collect(Collectors.toList());

        List<ContractEvent> logs = transaction.getOutputs()
                .stream()
                .map(wrapperOutput -> ((WrapperOutputWaves) wrapperOutput))
                .map(WrapperOutputWaves::getTransaction)
                .map(logBuilder::build)
                .flatMap(Collection::stream)
                .collect(Collectors.toList());

        return new WrapperTransactionReceipt(
                transaction.getHash(),
                contracts,
                logs,
                true
        );
    }
}
