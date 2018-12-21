package io.mywish.tron.blockchain.builders;

import io.lastwill.eventscan.model.NetworkProviderType;
import io.mywish.blockchain.ContractEvent;
import io.mywish.blockchain.ContractEventBuilder;
import io.mywish.troncli4j.model.EventResult;

import java.util.List;

public abstract class TronEventBuilder<T extends ContractEvent> extends ContractEventBuilder<T> {
    protected TronEventBuilder() {
        super(NetworkProviderType.TRON);
    }

    public T build(String address, List<Object> values) {
        throw new UnsupportedOperationException("Use other method with other signature.");
    }

    public abstract T build(String address, EventResult event);
}
