package io.lastwill.eventscan.events.builders.investmentPool;

import io.lastwill.eventscan.events.model.contract.investmentPool.SetInvestmentAddressEvent;
import io.mywish.wrapper.ContractEventBuilder;
import io.mywish.wrapper.ContractEventDefinition;
import io.mywish.wrapper.WrapperType;
import org.springframework.stereotype.Component;
import org.web3j.abi.datatypes.Address;

import java.util.Collections;
import java.util.List;

@Component
public class SetInvestmentAddressEventBuilder extends ContractEventBuilder<SetInvestmentAddressEvent> {
    private final static ContractEventDefinition definition = new ContractEventDefinition(
            "SetInvestmentAddress",
            Collections.singletonList(
                    WrapperType.create(Address.class, true)
            )
    );

    @Override
    public SetInvestmentAddressEvent build(String address, List<Object> values) {
        return new SetInvestmentAddressEvent(
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
