package io.lastwill.eventscan.services.builders.erc20;

import io.lastwill.eventscan.events.contract.ContractEventDefinition;
import io.lastwill.eventscan.events.contract.erc20.TransferEvent;
import io.lastwill.eventscan.services.builders.ContractEventBuilder;
import lombok.Getter;
import org.springframework.stereotype.Component;
import org.web3j.abi.TypeReference;
import org.web3j.abi.datatypes.Address;
import org.web3j.abi.datatypes.Type;
import org.web3j.abi.datatypes.Uint;
import org.web3j.protocol.core.methods.response.TransactionReceipt;

import java.math.BigInteger;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

@Component
@Getter
public class TransferEventBuilder extends ContractEventBuilder<TransferEvent> {
    private final ContractEventDefinition definition = new ContractEventDefinition(
            "Transfer",
            Arrays.asList(TypeReference.create(Address.class), TypeReference.create(Address.class)),
            Collections.singletonList(TypeReference.create(Uint.class))
    );

    @Override
    public TransferEvent build(TransactionReceipt transactionReceipt, String address, List<Type> indexedValues, List<Type> nonIndexedValues) {
        return new TransferEvent(definition, transactionReceipt, (String) indexedValues.get(0).getValue(), (String) indexedValues.get(1).getValue(), (BigInteger) nonIndexedValues.get(0).getValue(), address);
    }
}
