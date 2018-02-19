package io.lastwill.eventscan.services;

import io.lastwill.eventscan.events.contract.ContractEvent;
import io.lastwill.eventscan.services.builders.ContractEventBuilder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.web3j.abi.FunctionReturnDecoder;
import org.web3j.abi.TypeReference;
import org.web3j.abi.datatypes.*;
import org.web3j.protocol.core.methods.response.Log;
import org.web3j.protocol.core.methods.response.TransactionReceipt;

import javax.annotation.PostConstruct;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Slf4j
@Component
public class EventParser {
    @Autowired
    private List<ContractEventBuilder<?>> builders = new ArrayList<>();

    @PostConstruct
    protected void init() throws Exception {
        for (ContractEventBuilder<?> eventBuilder: builders) {
            if (events.containsKey(eventBuilder.getEventSignature())) {
                throw new Exception("Duplicate builder " + eventBuilder.getClass() + " with signature " + eventBuilder.getEventSignature());
            }
            events.put(eventBuilder.getEventSignature(), eventBuilder);
        }
    }

    private final Map<String, ContractEventBuilder<?>> events = new HashMap<>();

    public List<ContractEvent> parseEvents(TransactionReceipt transactionReceipt) {
        return transactionReceipt
                .getLogs()
                .stream()
                .map(Try(log -> parseEvent(transactionReceipt, log)))
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }

    public List<ContractEvent> parseEvents(final TransactionReceipt transactionReceipt, final String eventSignature) {
        return transactionReceipt
                .getLogs()
                .stream()
                .filter(log -> log.getTopics().size() < 1)
                .filter(log -> eventSignature.equalsIgnoreCase(log.getTopics().get(0)))
                .map(Try(log -> parseEvent(transactionReceipt, log)))
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }

    public ContractEvent parseEvent(final TransactionReceipt transactionReceipt, final Log log) {
        List<String> topics = log.getTopics();
        if (topics.size() < 1) {
            throw new IllegalArgumentException("Log.topics must be at least 1");
        }
        ContractEventBuilder<?> builder = events.get(log.getTopics().get(0));
        if (builder == null) {
            return null;
        }

        List<Type> indexedValues = new ArrayList<>();
        List<Type> nonIndexedValues = FunctionReturnDecoder.decode(
                log.getData(),
                builder.getNonIndexedParameters()
        );

        List<TypeReference<Type>> indexedParameters = builder.getIndexedParameters();
        for (int i = 0; i < indexedParameters.size(); i++) {
            Type value = FunctionReturnDecoder.decodeIndexedValue(
                    topics.get(i + 1),
                    indexedParameters.get(i)
            );
            indexedValues.add(value);
        }


        return builder.build(transactionReceipt, log.getAddress(), indexedValues, nonIndexedValues);
    }

    public <T, R> Function<T, R> Try(Function<T, R> func) {
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
