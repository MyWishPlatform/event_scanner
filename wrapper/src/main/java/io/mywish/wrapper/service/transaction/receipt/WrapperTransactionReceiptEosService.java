package io.mywish.wrapper.service.transaction.receipt;

import io.mywish.eoscli4j.model.Transaction;
import io.mywish.eoscli4j.model.EosAction;
import io.mywish.eoscli4j.model.TransactionStatus;
import io.mywish.wrapper.ContractEvent;
import io.mywish.wrapper.WrapperTransaction;
import io.mywish.wrapper.WrapperTransactionReceipt;
import io.mywish.wrapper.model.output.WrapperOutputEos;
import io.mywish.wrapper.service.log.WrapperLogEosService;
import io.mywish.wrapper.transaction.WrapperTransactionEos;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Slf4j
@Component
public class WrapperTransactionReceiptEosService {
    @Autowired
    private WrapperLogEosService logBuilder;

    public WrapperTransactionReceipt build(WrapperTransaction wrapperTransaction) {
        WrapperTransactionEos transaction = (WrapperTransactionEos) wrapperTransaction;
        List<String> contracts = Collections.emptyList();
        List<ContractEvent> logs = transaction
                .getOutputs()
                .stream()
                .filter(wrapperOutput -> wrapperOutput instanceof WrapperOutputEos)
                .map(wrapperOutput -> (WrapperOutputEos) wrapperOutput)
                .map(logBuilder::build)
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
        Boolean success = transaction.getStatus() == TransactionStatus.Executed;

        return new WrapperTransactionReceipt(
                wrapperTransaction.getHash(),
                contracts,
                logs,
                success
        );
    }
}
