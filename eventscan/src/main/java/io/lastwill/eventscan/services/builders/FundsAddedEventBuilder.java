package io.lastwill.eventscan.services.builders;

import io.lastwill.eventscan.events.contract.ContractEventDefinition;
import io.lastwill.eventscan.events.contract.FundsAddedEvent;
import lombok.Getter;
import org.springframework.stereotype.Component;
import org.web3j.abi.TypeReference;
import org.web3j.abi.datatypes.Address;
import org.web3j.abi.datatypes.Type;
import org.web3j.abi.datatypes.Uint;
import org.web3j.protocol.core.methods.response.TransactionReceipt;

import java.math.BigInteger;
import java.util.Collections;
import java.util.List;

@Getter
@Component
public class FundsAddedEventBuilder extends ContractEventBuilder<FundsAddedEvent> {
    private final ContractEventDefinition definition = new ContractEventDefinition(
            "FundsAdded",
            Collections.singletonList(TypeReference.create(Address.class)),
            Collections.singletonList(TypeReference.create(Uint.class))
    );

    @Override
    public FundsAddedEvent build(TransactionReceipt transactionReceipt, String address, List<Type> indexedValues, List<Type> nonIndexedValues) {
        return new FundsAddedEvent(definition, transactionReceipt, (String) indexedValues.get(0).getValue(), (BigInteger) nonIndexedValues.get(0).getValue(), address);
    }
}
