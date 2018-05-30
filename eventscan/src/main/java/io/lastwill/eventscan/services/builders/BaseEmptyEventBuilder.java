package io.lastwill.eventscan.services.builders;

import io.mywish.wrapper.ContractEventBuilder;
import io.mywish.wrapper.WrapperTransactionReceipt;
import io.lastwill.eventscan.events.contract.BaseEmptyEvent;
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
    public T build(final WrapperTransactionReceipt transactionReceipt, String address, final List<Object> indexedValues) {
        return buildEmpty(definition, address, transactionReceipt);
    }

    protected abstract T buildEmpty(final ContractEventDefinition definition, String address, final WrapperTransactionReceipt transactionReceipt);
}
