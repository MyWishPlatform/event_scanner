package io.lastwill.eventscan.services;

import com.glowstick.neocli4j.Event;
import io.lastwill.eventscan.events.contract.ContractEvent;
import io.lastwill.eventscan.services.builders.ContractEventBuilder;
import io.mywish.scanner.WrapperLog;
import io.mywish.scanner.WrapperTransaction;
import io.mywish.scanner.WrapperTransactionReceipt;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.web3j.abi.FunctionReturnDecoder;
import org.web3j.abi.TypeReference;
import org.web3j.abi.datatypes.*;
import org.web3j.protocol.core.methods.response.Log;

import javax.annotation.PostConstruct;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Slf4j
@Component
public class EventParser {
    @Autowired
    private List<ContractEventBuilder<?>> builders = new ArrayList<>();

    private Map<String, ContractEventBuilder<?>> eventsByName = new HashMap<>();

    @PostConstruct
    protected void init() throws Exception {
        for (ContractEventBuilder<?> eventBuilder: builders) {
            if (events.containsKey(eventBuilder.getEventSignature())) {
                throw new Exception("Duplicate builder " + eventBuilder.getClass() + " with signature " + eventBuilder.getEventSignature());
            }
            events.put(eventBuilder.getEventSignature(), eventBuilder);
            if (eventsByName.containsKey(eventBuilder.getEventName())) {
                throw new Exception("Duplicate builder " + eventBuilder.getClass() + " with name " + eventBuilder.getEventName());
            }
            eventsByName.put(eventBuilder.getEventName(), eventBuilder);
        }
    }

    private final Map<String, ContractEventBuilder<?>> events = new HashMap<>();

    public List<ContractEvent> parseEvents(WrapperTransactionReceipt transactionReceipt) {
        return transactionReceipt
                .getLogs()
                .stream()
                .map(Try(log -> parseEvent(transactionReceipt, log)))
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }

    public List<ContractEvent> parseEvents(final WrapperTransactionReceipt transactionReceipt, final String eventSignature) {
        return transactionReceipt
                .getLogs()
                .stream()
                .filter(log -> log.getTopics().size() > 0)
                .filter(log -> eventSignature.equalsIgnoreCase(log.getTopics().get(0)))
                .map(Try(log -> parseEvent(transactionReceipt, log)))
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }

    public ContractEvent parseEvent(final WrapperTransactionReceipt transactionReceipt, final WrapperLog log) {
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

    public ContractEvent parseEventNeo(Event event) {
        System.out.println(event.getName());
        ContractEventBuilder<?> builder = eventsByName.get(event.getName());
        if (builder == null) {
            return null;
        }
        return builder.build(null, event.getContract(), event.getArguments());
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
