package io.mywish.blockchain;

import io.lastwill.eventscan.model.NetworkProviderType;
import lombok.Getter;

import java.util.List;

@Getter
public abstract class ContractEventBuilder<T extends ContractEvent> {
    private final NetworkProviderType networkProviderType;

    protected ContractEventBuilder(NetworkProviderType networkProviderType) {
        this.networkProviderType = networkProviderType;
    }

    public abstract T build(String address, List<Object> values);

    abstract public ContractEventDefinition getDefinition();
}
