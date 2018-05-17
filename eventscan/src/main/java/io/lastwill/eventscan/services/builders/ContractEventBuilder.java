package io.lastwill.eventscan.services.builders;

import io.lastwill.eventscan.events.contract.ContractEvent;
import io.lastwill.eventscan.events.contract.ContractEventDefinition;
import io.mywish.scanner.WrapperTransactionReceipt;
import org.web3j.abi.TypeReference;
import org.web3j.abi.datatypes.Type;

import java.util.List;

public abstract class ContractEventBuilder<T extends ContractEvent> {
    public abstract T build(WrapperTransactionReceipt transactionReceipt, String address, List<Type> indexedValues, List<Type> nonIndexedValues);
    public abstract T build(WrapperTransactionReceipt transactionReceipt, String address, List<String> values);

    protected abstract ContractEventDefinition getDefinition();

    public String getEventSignature() {
        return getDefinition().getSignature();
    }

    public String getEventName() {
        return getDefinition().getEvent().getName();
    }

    public List<TypeReference<Type>> getIndexedParameters() {
        return getDefinition().getEvent().getIndexedParameters();
    }

    public List<TypeReference<Type>> getNonIndexedParameters() {
        return getDefinition().getEvent().getNonIndexedParameters();
    }
}
