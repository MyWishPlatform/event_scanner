package io.mywish.wrapper.service.transaction.receipt;

import io.mywish.neocli4j.Event;
import io.mywish.wrapper.ContractEvent;
import io.mywish.wrapper.ContractEventDefinition;
import io.mywish.wrapper.WrapperLog;
import io.mywish.wrapper.WrapperTransactionReceipt;
import io.mywish.wrapper.service.log.WrapperLogNeoService;
import io.mywish.wrapper.transaction.WrapperTransactionNeo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.web3j.protocol.core.methods.response.Log;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

@Slf4j
@Component
public class WrapperTransactionReceiptNeoService {
    @Autowired
    private WrapperLogNeoService logBuilder;

    public WrapperTransactionReceipt build(WrapperTransactionNeo transaction, List<Event> events) {
        String hash = transaction.getHash();
        List<String> contracts = transaction.getContracts();
        List<ContractEvent> logs = events
                .stream()
                .map(this::buildEvent)
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

    private ContractEvent buildEvent(Event event) {
        try {
            return logBuilder.build(event);
        }
        catch (Exception e) {
            log.warn("Impossible to build event from NEO event {}, contract {}.", event.getName(), event.getContract(), e);
            return null;
        }
    }

}
