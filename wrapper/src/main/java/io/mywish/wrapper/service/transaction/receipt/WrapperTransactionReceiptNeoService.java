package io.mywish.wrapper.service.transaction.receipt;

import io.mywish.neocli4j.Event;
import io.mywish.wrapper.ContractEventDefinition;
import io.mywish.wrapper.WrapperLog;
import io.mywish.wrapper.WrapperTransactionReceipt;
import io.mywish.wrapper.service.log.WrapperLogNeoService;
import io.mywish.wrapper.transaction.WrapperTransactionNeo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

@Component
public class WrapperTransactionReceiptNeoService {
    @Autowired
    WrapperLogNeoService logBuilder;

    public WrapperTransactionReceipt build(WrapperTransactionNeo transaction, List<Event> events, Map<String, ContractEventDefinition> definitionsByName) {
        String hash = transaction.getHash();
        List<String> contracts = transaction.getContracts();
        List<WrapperLog> logs = events
                .stream()
                .map(event -> logBuilder.build(event, definitionsByName))
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
        // todo detect failures
        boolean success = true;
        return new WrapperTransactionReceipt(
                hash,
                contracts,
                logs,
                success
        );
    }
}
