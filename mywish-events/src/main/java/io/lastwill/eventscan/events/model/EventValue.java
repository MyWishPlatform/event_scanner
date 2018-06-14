package io.lastwill.eventscan.model;

import lombok.Getter;
import org.web3j.abi.EventValues;
import org.web3j.abi.datatypes.Event;
import org.web3j.abi.datatypes.Type;

import java.util.List;

@Getter
public class EventValue extends EventValues {
    private final Event event;

    public EventValue(Event event, List<Type> indexedValues, List<Type> nonIndexedValues) {
        super(indexedValues, nonIndexedValues);
        this.event = event;
    }
}
