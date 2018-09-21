package io.lastwill.eventscan.events.builders;

import io.mywish.blockchain.ContractEventBuilder;
import io.mywish.blockchain.WrapperType;
import io.mywish.blockchain.ContractEventDefinition;
import io.lastwill.eventscan.events.model.contract.OwnershipTransferredEvent;
import org.springframework.stereotype.Component;
import org.web3j.abi.datatypes.Address;
import java.util.Arrays;
import java.util.List;

@Component
public class OwnershipTransferredEventBuilder extends ContractEventBuilder<OwnershipTransferredEvent> {
    private final static ContractEventDefinition DEFINITION = new ContractEventDefinition(
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
    public ContractEventDefinition getDefinition() {
        return DEFINITION;
    }
}
