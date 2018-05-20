package io.mywish.wrapper;

import java.util.List;

public abstract class ContractEventBuilder<T extends ContractEvent> {
    public abstract T build(WrapperTransactionReceipt transactionReceipt, String address, List<Object> values);
    abstract public ContractEventDefinition getDefinition();
}
