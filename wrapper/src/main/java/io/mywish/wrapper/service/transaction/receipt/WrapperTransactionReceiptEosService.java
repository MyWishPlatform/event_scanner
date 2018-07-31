package io.mywish.wrapper.service.transaction.receipt;

import io.mywish.eoscli4j.model.Transaction;
import io.mywish.eoscli4j.model.TransactionAction;
import io.mywish.wrapper.ContractEvent;
import io.mywish.wrapper.WrapperTransactionReceipt;
import io.mywish.wrapper.service.log.WrapperLogEosService;
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

    public WrapperTransactionReceipt build(Transaction transaction) {
        String hash = transaction.getTrx().getId();
        List<String> contracts = Collections.emptyList();
        List<ContractEvent> logs = transaction
                .getTrx()
                .getTransaction()
                .getActions()
                .stream()
                .map(this::buildEvent)
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
        Boolean success = "executed".equals(transaction.getStatus());

        return new WrapperTransactionReceipt(
                hash,
                contracts,
                logs,
                success
        );
    }

    private ContractEvent buildEvent(TransactionAction action) {
        try {
            return logBuilder.build(action);
        }
        catch (Exception e) {
            log.warn("Impossible to build event from log with name {}.", action.getName(), e);
            return null;
        }
    }
}
