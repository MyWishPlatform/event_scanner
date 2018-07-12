package io.lastwill.eventscan.events.builders.investmentPool;

import io.lastwill.eventscan.events.model.contract.investmentPool.SetTokenAddressEvent;
import io.mywish.wrapper.ContractEventBuilder;
import io.mywish.wrapper.ContractEventDefinition;
import io.mywish.wrapper.WrapperType;
import org.springframework.stereotype.Component;
import org.web3j.abi.datatypes.Address;

import java.util.Collections;
import java.util.List;

@Component
public class SetTokenAddressEventBuilder extends ContractEventBuilder<SetTokenAddressEvent> {
    private final static ContractEventDefinition definition = new ContractEventDefinition(
            "SetTokenAddress",
            Collections.singletonList(
                    WrapperType.create(Address.class, true)
            )
    );

    @Override
    public SetTokenAddressEvent build(String address, List<Object> values) {
        return new SetTokenAddressEvent(
                definition,
                address,
                (String) values.get(0)
        );
    }

    @Override
    public ContractEventDefinition getDefinition() {
        return definition;
    }
}
