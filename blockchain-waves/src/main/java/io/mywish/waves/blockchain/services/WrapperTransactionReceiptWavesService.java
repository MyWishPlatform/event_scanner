package io.mywish.waves.blockchain.services;

import io.mywish.blockchain.ContractEvent;
import io.mywish.blockchain.WrapperTransactionReceipt;
import io.mywish.waves.blockchain.model.WrapperTransactionWaves;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;

@Slf4j
@Component
public class WrapperTransactionReceiptWavesService {
    @Autowired
    private WrapperLogWavesService logBuilder;

    public WrapperTransactionReceipt build(WrapperTransactionWaves wrapperTransaction) {
        List<String> contracts = Collections.emptyList();
        List<ContractEvent> logs = logBuilder.build(wrapperTransaction);

        return new WrapperTransactionReceipt(
                wrapperTransaction.getHash(),
                contracts,
                logs,
                true
        );
    }
}
