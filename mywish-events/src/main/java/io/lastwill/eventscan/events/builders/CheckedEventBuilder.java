package io.lastwill.eventscan.events.builders;

import io.mywish.blockchain.ContractEventBuilder;
import io.mywish.blockchain.WrapperType;
import io.lastwill.eventscan.events.model.contract.CheckedEvent;
import io.mywish.blockchain.ContractEventDefinition;
import lombok.Getter;
import org.springframework.stereotype.Component;
import org.web3j.abi.datatypes.Bool;
import java.util.Arrays;
import java.util.List;

@Getter
@Component
public class CheckedEventBuilder extends ContractEventBuilder<CheckedEvent> {
    private final ContractEventDefinition definition = new ContractEventDefinition(
            "Checked",
            Arrays.asList(WrapperType.create(Bool.class, false))
    );

    @Override
    public CheckedEvent build(String address, List<Object> values) {
        return new CheckedEvent(definition, (Boolean) values.get(0), address);
    }
}
