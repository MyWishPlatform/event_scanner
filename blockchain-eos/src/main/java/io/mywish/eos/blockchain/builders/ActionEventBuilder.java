package io.mywish.eos.blockchain.builders;

import com.fasterxml.jackson.databind.node.ObjectNode;
import io.lastwill.eventscan.model.NetworkProviderType;
import io.mywish.blockchain.ContractEvent;
import io.mywish.blockchain.ContractEventBuilder;

import java.util.List;

public abstract class ActionEventBuilder<T extends ContractEvent> extends ContractEventBuilder<T> {
    protected ActionEventBuilder() {
        super(NetworkProviderType.EOS);
    }

    public T build(String address, List<Object> values) {
        throw new UnsupportedOperationException("Use other method with other signature.");
    }

    public abstract T build(String address, ObjectNode data);
}
