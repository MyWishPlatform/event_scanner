package io.mywish.blockchain.service.transaction.receipt;

import io.mywish.neocli4j.Event;
import io.mywish.blockchain.ContractEvent;
import io.mywish.blockchain.WrapperTransactionReceipt;
import io.mywish.blockchain.service.log.WrapperLogNeoService;
import io.mywish.blockchain.transaction.WrapperTransactionNeo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
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
