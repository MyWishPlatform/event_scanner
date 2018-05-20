package io.mywish.wrapper;

import io.mywish.wrapper.WrapperType;
import lombok.Getter;
import org.web3j.abi.EventEncoder;
import org.web3j.abi.datatypes.Event;

import java.util.List;
import java.util.stream.Collectors;

@Getter
public class ContractEventDefinition {
    private final String name;
    private final List<WrapperType<?>> types;
    private final String signature;
    private final Event event;

    public ContractEventDefinition(String name, List<WrapperType<?>> types) {
        this.name = name;
        this.types = types;
        this.event = new Event(
                name,
                types.stream().filter(WrapperType::isIndexed).map(WrapperType::getTypeReference).collect(Collectors.toList()),
                types.stream().filter(type -> !type.isIndexed()).map(WrapperType::getTypeReference).collect(Collectors.toList())
        );
        this.signature = EventEncoder.encode(event);
    }
}
