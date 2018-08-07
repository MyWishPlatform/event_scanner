package io.mywish.wrapper.service.transaction.receipt;

import io.mywish.eoscli4j.model.Transaction;
import io.mywish.eoscli4j.model.EosAction;
import io.mywish.eoscli4j.model.TransactionStatus;
import io.mywish.wrapper.ContractEvent;
import io.mywish.wrapper.WrapperTransaction;
import io.mywish.wrapper.WrapperTransactionReceipt;
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
//    @Autowired
//    private WrapperLogEosService logBuilder;

    public WrapperTransactionReceipt build(WrapperTransaction wrapperTransaction) {
        Transaction transaction = ((WrapperTransactionEos) wrapperTransaction).getNativeTransaction();
        String hash = transaction.getId();
        List<String> contracts = Collections.emptyList();
//        List<ContractEvent> logs = transaction
//                .getActions()
//                .stream()
//                .map(this::buildEvent)
//                .filter(Objects::nonNull)
//                .collect(Collectors.toList());
        Boolean success = transaction.getStatus() == TransactionStatus.Executed;

        return new WrapperTransactionReceipt(
                hash,
                contracts,
                Collections.emptyList(),
                success
        );
    }

//    private ContractEvent buildEvent(EosAction action) {
//        try {
//            return logBuilder.build(action);
//        }
//        catch (Exception e) {
//            log.warn("Impossible to build event from log with name {}.", action.getName(), e);
//            return null;
//        }
//    }
}
