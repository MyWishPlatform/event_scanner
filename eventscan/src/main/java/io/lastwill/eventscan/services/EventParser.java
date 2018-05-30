package io.lastwill.eventscan.services;

import io.mywish.wrapper.ContractEvent;
import io.mywish.wrapper.ContractEventBuilder;
import io.mywish.wrapper.WrapperLog;
import io.mywish.wrapper.WrapperTransactionReceipt;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import javax.annotation.PostConstruct;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Slf4j
@Component
public class EventParser {
    @Autowired
    private List<ContractEventBuilder<?>> builders = new ArrayList<>();

    private Map<String, ContractEventBuilder<?>> buildersByName = new HashMap<>();

    @PostConstruct
    protected void init() throws Exception {
        for (ContractEventBuilder<?> eventBuilder: builders) {
            String name = eventBuilder.getDefinition().getName();
            if (buildersByName.containsKey(name)) {
                throw new Exception("Duplicate builder " + eventBuilder.getClass() + " with name " + name);
            }
            buildersByName.put(name, eventBuilder);
        }
    }

    public List<ContractEvent> parseEvents(WrapperTransactionReceipt transactionReceipt) {
        return transactionReceipt
                .getLogs()
                .stream()
                .map(Try(log -> parseEvent(transactionReceipt, log)))
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }

    private ContractEvent parseEvent(final WrapperTransactionReceipt transactionReceipt, final WrapperLog log) {
        ContractEventBuilder<?> builder = buildersByName.get(log.getName());
        if (builder == null) {
            return null;
        }
        return builder.build(transactionReceipt, log.getAddress(), log.getArgs());
    }

    private <T, R> Function<T, R> Try(Function<T, R> func) {
        return (a) -> {
            try {
                return func.apply(a);
            }
            catch (Exception e) {
                log.warn("Parsing event failed.", e);
                return null;
            }
        };
    }
}
