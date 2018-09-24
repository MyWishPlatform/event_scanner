package io.mywish.web3.blockchain.service;

import io.mywish.blockchain.ContractEvent;
import io.mywish.blockchain.WrapperTransactionReceipt;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.web3j.protocol.core.methods.response.Log;
import org.web3j.protocol.core.methods.response.TransactionReceipt;
import org.web3j.utils.Numeric;

import java.math.BigInteger;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Slf4j
@Component
public class WrapperTransactionReceiptWeb3Service {
    @Autowired
    private WrapperLogWeb3Service logBuilder;

    private boolean isSuccess(TransactionReceipt receipt) {
        BigInteger status;
        if (receipt.getStatus().startsWith("0x")) {
            status = Numeric.decodeQuantity(receipt.getStatus());
        }
        else {
            status = new BigInteger(receipt.getStatus());
        }
        return status.compareTo(BigInteger.ZERO) != 0;
    }

    public WrapperTransactionReceipt build(TransactionReceipt receipt) {
        String hash = receipt.getTransactionHash();
        List<String> contracts = Collections.singletonList(receipt.getContractAddress());
        List<ContractEvent> logs = receipt
                .getLogs()
                .stream()
                .map(this::buildEvent)
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
        return new WrapperTransactionReceipt(
                hash,
                contracts,
                logs,
                isSuccess(receipt)
        );
    }

    private ContractEvent buildEvent(Log eventLog) {
        try {
            return logBuilder.build(eventLog);
        }
        catch (Exception e) {
            log.warn("Impossible to build event from log with signature {}, tx {}.", eventLog.getTopics().get(0), eventLog.getTransactionHash(), e);
            return null;
        }
    }
}
