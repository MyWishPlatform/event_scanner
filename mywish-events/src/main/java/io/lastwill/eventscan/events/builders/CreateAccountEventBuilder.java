package io.lastwill.eventscan.events.builders;

import io.lastwill.eventscan.events.model.contract.CreateAccountEvent;
import io.mywish.wrapper.ContractEventBuilder;
import io.mywish.wrapper.ContractEventDefinition;
import io.mywish.wrapper.WrapperType;
import org.springframework.stereotype.Component;
import org.web3j.abi.datatypes.Address;

import java.util.Arrays;
import java.util.List;

@Component
public class CreateAccountEventBuilder extends ContractEventBuilder<CreateAccountEvent> {
    private final static ContractEventDefinition DEFINITION = new ContractEventDefinition(
            "eosio::newaccount", // it just a stub event name, do not change it!
            Arrays.asList(
                    WrapperType.create(Address.class, true),
                    WrapperType.create(Address.class, true)
            )
    );

    @Override
    public CreateAccountEvent build(String address, List<Object> values) {
        return new CreateAccountEvent(DEFINITION, address, (String) values.get(0), (String) values.get(1));
    }

    @Override
    public ContractEventDefinition getDefinition() {
        return DEFINITION;
    }
}