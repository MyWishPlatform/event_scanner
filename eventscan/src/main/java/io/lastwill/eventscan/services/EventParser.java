package io.lastwill.eventscan.services;

import com.sun.org.apache.bcel.internal.generic.NEW;
import io.lastwill.eventscan.model.EventValue;
import org.springframework.stereotype.Component;
import org.web3j.abi.EventEncoder;
import org.web3j.abi.FunctionReturnDecoder;
import org.web3j.abi.TypeReference;
import org.web3j.abi.datatypes.*;
import org.web3j.protocol.core.methods.response.Log;
import org.web3j.protocol.core.methods.response.TransactionReceipt;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Component
public class EventParser {
    public final Event Checked = new Event(
            "Checked",
            Collections.emptyList(),
            Collections.singletonList(TypeReference.create(Bool.class))
    );

    public final Event NeedRepeatCheck = new Event(
            "NeedRepeatCheck",
            Collections.emptyList(),
            Collections.singletonList(TypeReference.create(Bool.class))
    );

    public final Event Price = new Event(
            "Price",
            Collections.emptyList(),
            Collections.singletonList(TypeReference.create(Uint.class))
    );

    public final Event Killed = new Event(
            "Killed",
            Collections.emptyList(),
            Collections.singletonList(TypeReference.create(Bool.class))
    );

    public final Event FundsAdded = new Event(
            "FundsAdded",
            Collections.singletonList(TypeReference.create(Address.class)),
            Collections.singletonList(TypeReference.create(Uint.class))
    );

    public final Event Triggered = new Event(
            "Triggered",
            Collections.emptyList(),
            Collections.singletonList(TypeReference.create(Uint.class))
    );

    /**
     * event Transfer(address indexed from, address indexed to, uint256 value);
     */
    public final Event TransferERC20 = new Event(
            "Transfer",
            Arrays.asList(TypeReference.create(Address.class), TypeReference.create(Address.class)),
            Collections.singletonList(TypeReference.create(Uint.class))
    );

    /**
     *  event OwnershipTransferred(address indexed previousOwner, address indexed newOwner);
     */
    public final Event OwnershipTransferred = new Event(
            "OwnershipTransferred",
            Arrays.asList(TypeReference.create(Address.class), TypeReference.create(Address.class)),
            Collections.emptyList()
    );

    /**
     * Crowdsale initialized event.
     */
    public final Event Initialized = new Event(
            "Initialized",
            Collections.emptyList(),
            Collections.emptyList()
    );

    private Map<String, Event> events = new HashMap<String, Event>() {{
        put(EventEncoder.encode(Checked), Checked);
        put(EventEncoder.encode(NeedRepeatCheck), NeedRepeatCheck);
        put(EventEncoder.encode(Price), Price);
        put(EventEncoder.encode(Killed), Killed);
        put(EventEncoder.encode(FundsAdded), FundsAdded);
        put(EventEncoder.encode(Triggered), Triggered);
        put(EventEncoder.encode(TransferERC20), TransferERC20);
        put(EventEncoder.encode(OwnershipTransferred), OwnershipTransferred);
        put(EventEncoder.encode(Initialized), Initialized);
    }};

    public List<EventValue> parseEvents(TransactionReceipt transactionReceipt) {
        return transactionReceipt
                .getLogs()
                .stream()
                .map(Try(this::parseEvent))
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }

    public List<EventValue> parseEvents(final TransactionReceipt transactionReceipt, final Event event) {
        return transactionReceipt
                .getLogs()
                .stream()
                .map(Try(this::parseEvent))
                .filter(Objects::nonNull)
                .filter(eventValue -> event.equals(eventValue.getEvent()))
                .collect(Collectors.toList());
    }

    public EventValue parseEvent(Log log) {
        List<String> topics = log.getTopics();
        if (topics.size() < 1) {
            throw new IllegalArgumentException("Log.topics must be at least 1");
        }
        Event event = events.get(log.getTopics().get(0));
        if (event == null) {
            return null;
        }

        List<Type> indexedValues = new ArrayList<>();
        List<Type> nonIndexedValues = FunctionReturnDecoder.decode(
                log.getData(),
                event.getNonIndexedParameters()
        );

        List<TypeReference<Type>> indexedParameters = event.getIndexedParameters();
        for (int i = 0; i < indexedParameters.size(); i++) {
            Type value = FunctionReturnDecoder.decodeIndexedValue(
                    topics.get(i + 1),
                    indexedParameters.get(i)
            );
            indexedValues.add(value);
        }


        return new EventValue(event, indexedValues, nonIndexedValues);
    }

    public <T, R> Function<T, R> Try(Function<T, R> func) {
        return (a) -> {
            try {
                return func.apply(a);
            }
            catch (Exception ignored) {
                return null;
            }
        };
    }
}
