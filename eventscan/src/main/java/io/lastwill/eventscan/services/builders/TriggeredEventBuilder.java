package io.lastwill.eventscan.services.builders;

import io.lastwill.eventscan.events.contract.ContractEventDefinition;
import io.lastwill.eventscan.events.contract.TriggeredEvent;
import lombok.Getter;
import org.springframework.stereotype.Component;
import org.web3j.abi.TypeReference;
import org.web3j.abi.datatypes.Type;
import org.web3j.abi.datatypes.Uint;
import org.web3j.protocol.core.methods.response.TransactionReceipt;

import java.math.BigInteger;
import java.util.Collections;
import java.util.List;

@Getter
@Component
public class TriggeredEventBuilder extends ContractEventBuilder<TriggeredEvent> {
    private final ContractEventDefinition definition = new ContractEventDefinition(
            "Triggered",
            Collections.emptyList(),
            Collections.singletonList(TypeReference.create(Uint.class))
    );

    @Override
    public TriggeredEvent build(TransactionReceipt transactionReceipt, String address, List<Type> indexedValues, List<Type> nonIndexedValues) {
        return new TriggeredEvent(definition, transactionReceipt, (BigInteger) nonIndexedValues.get(0).getValue(), address);
    }
}
