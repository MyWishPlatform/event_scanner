package io.lastwill.eventscan.events.contract;

import lombok.Getter;
import org.web3j.abi.EventEncoder;
import org.web3j.abi.TypeReference;
import org.web3j.abi.datatypes.Event;

import java.util.List;

@Getter
public class ContractEventDefinition {
    private final String signature;
    private final Event event;

    public ContractEventDefinition(String name, List<TypeReference<?>> indexedParameters, List<TypeReference<?>> nonIndexedParameters) {
        event = new Event(name, indexedParameters, nonIndexedParameters);
        signature = EventEncoder.encode(event);
    }
}
