package io.mywish.wrapper.service.transaction.receipt;

import io.mywish.wrapper.ContractEventDefinition;
import io.mywish.wrapper.WrapperLog;
import io.mywish.wrapper.WrapperTransactionReceipt;
import io.mywish.wrapper.service.log.WrapperLogWeb3Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.web3j.protocol.core.methods.response.TransactionReceipt;
import org.web3j.utils.Numeric;

import java.math.BigInteger;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

@Component
public class WrapperTransactionReceiptWeb3Service {
    @Autowired
    private WrapperLogWeb3Service logBuilder;

    private boolean isSuccess(TransactionReceipt receipt) {
        BigInteger status;
        if (receipt.getStatus().startsWith("0x")) {
            status = Numeric.decodeQuantity(receipt.getStatus());
        } else {
            status = new BigInteger(receipt.getStatus());
        }
        return status.compareTo(BigInteger.ZERO) != 0;
    }

    public WrapperTransactionReceipt build(TransactionReceipt receipt, Map<String, ContractEventDefinition> eventDefinitionsBySignature) {
        String hash = receipt.getTransactionHash();
        List<String> contracts = Collections.singletonList(receipt.getContractAddress());
        List<WrapperLog> logs =
                receipt.getLogs().stream().map(log -> {
                    String signature = log.getTopics().get(0);
                    if (eventDefinitionsBySignature.containsKey(signature)) {
                        return logBuilder.build(log, eventDefinitionsBySignature.get(signature));
                    }
                    return null;
                }).filter(Objects::nonNull).collect(Collectors.toList());
        return new WrapperTransactionReceipt(
                hash,
                contracts,
                logs,
                isSuccess(receipt)
        );
    }
}
