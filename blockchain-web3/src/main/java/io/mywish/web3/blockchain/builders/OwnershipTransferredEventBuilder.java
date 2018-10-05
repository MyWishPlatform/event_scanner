package io.mywish.web3.blockchain.builders;

import io.lastwill.eventscan.events.model.contract.OwnershipTransferredEvent;
import io.lastwill.eventscan.model.NetworkProviderType;
import io.mywish.web3.blockchain.model.Web3ContractEventDefinition;
import io.mywish.web3.blockchain.model.WrapperType;
import lombok.NoArgsConstructor;
import org.springframework.stereotype.Component;
import org.web3j.abi.datatypes.Address;

import java.util.Arrays;
import java.util.List;

@Component
@NoArgsConstructor
public class OwnershipTransferredEventBuilder extends Web3ContractEventBuilder<OwnershipTransferredEvent> {
    private final static Web3ContractEventDefinition DEFINITION = new Web3ContractEventDefinition(
            "OwnershipTransferred",
            Arrays.asList(
                    WrapperType.create(Address.class, true),
                    WrapperType.create(Address.class, true)
            )
    );

    @Override
    public OwnershipTransferredEvent build(String address, List<Object> values) {
        return new OwnershipTransferredEvent(
                DEFINITION,
                (String) values.get(0),
                (String) values.get(1),
                address
        );
    }

    @Override
    public Web3ContractEventDefinition getDefinition() {
        return DEFINITION;
    }
}
