package io.lastwill.eventscan.services.builders;

import io.lastwill.eventscan.events.contract.BaseEmptyEvent;
import io.lastwill.eventscan.events.contract.ContractEventDefinition;
import lombok.Getter;
import org.web3j.abi.datatypes.Type;
import org.web3j.protocol.core.methods.response.TransactionReceipt;

import java.util.Collections;
import java.util.List;

@Getter
public abstract class BaseEmptyEventBuilder<T extends BaseEmptyEvent> extends ContractEventBuilder<T> {
    private final ContractEventDefinition definition;

    public BaseEmptyEventBuilder(final String eventName) {
        definition = new ContractEventDefinition(
                eventName,
                Collections.emptyList(),
                Collections.emptyList()
        );
    }

    @Override
    public T build(final TransactionReceipt transactionReceipt, String address, final List<Type> indexedValues, final List<Type> nonIndexedValues) {
        return buildEmpty(definition, address, transactionReceipt);
    }

    @Override
    public T build(final TransactionReceipt transactionReceipt, String address, final List<String> values) {
        return buildEmpty(definition, address, transactionReceipt);
    }

    protected abstract T buildEmpty(final ContractEventDefinition definition, String address, final TransactionReceipt transactionReceipt);
}
