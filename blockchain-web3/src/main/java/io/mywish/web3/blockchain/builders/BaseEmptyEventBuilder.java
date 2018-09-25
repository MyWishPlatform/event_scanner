package io.mywish.web3.blockchain.builders;

import io.lastwill.eventscan.events.model.contract.BaseEmptyEvent;
import io.mywish.blockchain.ContractEventDefinition;
import io.mywish.web3.blockchain.model.Web3ContractEventDefinition;
import lombok.Getter;

import java.util.Collections;
import java.util.List;

@Getter
public abstract class BaseEmptyEventBuilder<T extends BaseEmptyEvent> extends Web3ContractEventBuilder<T> {
    private final Web3ContractEventDefinition definition;

    public BaseEmptyEventBuilder(final String eventName) {
        super();
        definition = new Web3ContractEventDefinition(
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
