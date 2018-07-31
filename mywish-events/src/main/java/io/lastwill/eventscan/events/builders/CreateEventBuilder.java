package io.lastwill.eventscan.events.builders;

import io.lastwill.eventscan.events.model.CreateEvent;
import io.mywish.wrapper.ContractEventBuilder;
import io.mywish.wrapper.ContractEventDefinition;
import io.mywish.wrapper.WrapperType;
import org.springframework.stereotype.Component;
import org.web3j.abi.datatypes.Type;

import java.util.Arrays;
import java.util.List;

@Component
public class CreateEventBuilder extends ContractEventBuilder<CreateEvent> {
    // TODO: move outside
    private class TypeString implements Type<String> {
        private String value;

        public TypeString(String value) {
            this.value = value;
        }

        @Override
        public String getTypeAsString() {
            return "string";
        }

        @Override
        public String getValue() {
            return value;
        }
    }

    private final static ContractEventDefinition DEFINITION = new ContractEventDefinition(
            "OwnershipTransferred",
            Arrays.asList(
                    WrapperType.create(TypeString.class, true),
                    WrapperType.create(TypeString.class, true)
            )
    );

    @Override
    public CreateEvent build(String address, List<Object> values) {
        return new CreateEvent(DEFINITION, address, (String)values.get(0), (String)values.get(1));
    }

    @Override
    public ContractEventDefinition getDefinition() {
        return DEFINITION;
    }
}