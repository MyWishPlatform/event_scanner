package io.lastwill.eventscan.events.builders;

import io.mywish.wrapper.ContractEventBuilder;
import io.lastwill.eventscan.events.model.contract.BaseEmptyEvent;
import io.mywish.wrapper.ContractEventDefinition;
import lombok.Getter;
import java.util.Collections;
import java.util.List;

@Getter
public abstract class BaseEmptyEventBuilder<T extends BaseEmptyEvent> extends ContractEventBuilder<T> {
    private final ContractEventDefinition definition;

    public BaseEmptyEventBuilder(final String eventName) {
        definition = new ContractEventDefinition(
                eventName,
                Collections.emptyList()
        );
    }

    @Override
    public T build(String address, final List<Object> indexedValues) {
        return buildEmpty(definition, address);
    }

    protected abstract T buildEmpty(final ContractEventDefinition definition, String address);
}
