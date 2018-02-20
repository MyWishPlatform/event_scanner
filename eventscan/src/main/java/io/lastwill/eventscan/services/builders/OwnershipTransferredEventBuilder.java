package io.lastwill.eventscan.services.builders;

import io.lastwill.eventscan.events.contract.ContractEventDefinition;
import io.lastwill.eventscan.events.contract.OwnershipTransferredEvent;
import org.springframework.stereotype.Component;
import org.web3j.abi.TypeReference;
import org.web3j.abi.datatypes.Address;
import org.web3j.abi.datatypes.Type;
import org.web3j.protocol.core.methods.response.TransactionReceipt;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

@Component
public class OwnershipTransferredEventBuilder extends ContractEventBuilder<OwnershipTransferredEvent> {
    private final static ContractEventDefinition DEFINITION = new ContractEventDefinition(
            "OwnershipTransferred",
            Arrays.asList(TypeReference.create(Address.class), TypeReference.create(Address.class)),
            Collections.emptyList()
    );

    @Override
    public OwnershipTransferredEvent build(TransactionReceipt transactionReceipt, String address, List<Type> indexedValues, List<Type> nonIndexedValues) {
        return new OwnershipTransferredEvent(DEFINITION, transactionReceipt, (String) indexedValues.get(0).getValue(), (String) indexedValues.get(1).getValue(), address);
    }

    @Override
    protected ContractEventDefinition getDefinition() {
        return DEFINITION;
    }
}
