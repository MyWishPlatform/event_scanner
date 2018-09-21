package io.lastwill.eventscan.events.builders;

import io.mywish.blockchain.ContractEventBuilder;
import io.mywish.blockchain.WrapperType;
import io.mywish.blockchain.ContractEventDefinition;
import io.lastwill.eventscan.events.model.contract.TriggeredEvent;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.web3j.abi.datatypes.Uint;

import java.util.Collections;
import java.util.List;

@Getter
@Component
public class TriggeredEventBuilder extends ContractEventBuilder<TriggeredEvent> {
    @Autowired
    private TypeHelper typeHelper;

    private final ContractEventDefinition definition = new ContractEventDefinition(
            "Triggered",
            Collections.singletonList(WrapperType.create(Uint.class, false))
    );

    @Override
    public TriggeredEvent build(String address, List<Object> values) {
        return new TriggeredEvent(definition, typeHelper.toBigInteger(values.get(0)), address);
    }
}
