package io.lastwill.eventscan.events.builders;

import io.mywish.wrapper.ContractEventBuilder;
import io.mywish.wrapper.WrapperType;
import io.mywish.wrapper.ContractEventDefinition;
import io.lastwill.eventscan.events.model.contract.TriggeredEvent;
import lombok.Getter;
import org.springframework.stereotype.Component;
import org.web3j.abi.datatypes.Uint;
import java.math.BigInteger;
import java.util.Collections;
import java.util.List;

@Getter
@Component
public class TriggeredEventBuilder extends ContractEventBuilder<TriggeredEvent> {
    private final ContractEventDefinition definition = new ContractEventDefinition(
            "Triggered",
            Collections.singletonList(WrapperType.create(Uint.class, false))
    );

    @Override
    public TriggeredEvent build(String address, List<Object> values) {
        return new TriggeredEvent(definition, (BigInteger) values.get(0), address);
    }
}
