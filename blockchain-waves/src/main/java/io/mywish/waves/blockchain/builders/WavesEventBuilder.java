package io.mywish.waves.blockchain.builders;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import io.lastwill.eventscan.model.NetworkProviderType;
import io.mywish.blockchain.ContractEvent;
import io.mywish.blockchain.ContractEventBuilder;

import java.util.List;

public abstract class WavesEventBuilder<T extends ContractEvent> extends ContractEventBuilder<T> {
    protected WavesEventBuilder() {
        super(NetworkProviderType.WAVES);
    }

    public T build(String address, List<Object> values) {
        throw new UnsupportedOperationException("Use other method with other signature.");
    }

    public abstract List<T> build(String address, ArrayNode args);
}
