package io.lastwill.eventscan.services.builders;

import io.lastwill.eventscan.events.contract.ContractEventDefinition;
import io.lastwill.eventscan.events.contract.KilledEvent;
import lombok.Getter;
import org.springframework.stereotype.Component;
import org.web3j.abi.TypeReference;
import org.web3j.abi.datatypes.Bool;
import org.web3j.abi.datatypes.Type;
import org.web3j.protocol.core.methods.response.TransactionReceipt;

import java.util.Collections;
import java.util.List;

@Getter
@Component
public class KilledEventBuilder extends ContractEventBuilder<KilledEvent> {
    private final ContractEventDefinition definition = new ContractEventDefinition(
            "Killed",
            Collections.emptyList(),
            Collections.singletonList(TypeReference.create(Bool.class))
    );

    @Override
    public KilledEvent build(TransactionReceipt transactionReceipt, String address, List<Type> indexedValues, List<Type> nonIndexedValues) {
        return new KilledEvent(definition, transactionReceipt, (Boolean) nonIndexedValues.get(0).getValue(), address);
    }

}
