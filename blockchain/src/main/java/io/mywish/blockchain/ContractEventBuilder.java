package io.mywish.blockchain;

import java.util.List;

public abstract class ContractEventBuilder<T extends ContractEvent> {
    public abstract T build(String address, List<Object> values);
    abstract public ContractEventDefinition getDefinition();
}
