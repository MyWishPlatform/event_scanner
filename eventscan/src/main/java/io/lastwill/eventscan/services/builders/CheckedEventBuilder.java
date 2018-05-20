package io.lastwill.eventscan.services.builders;

import io.mywish.wrapper.ContractEventBuilder;
import io.mywish.wrapper.WrapperTransactionReceipt;
import io.mywish.wrapper.WrapperType;
import io.lastwill.eventscan.events.contract.CheckedEvent;
import io.mywish.wrapper.ContractEventDefinition;
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
    public CheckedEvent build(WrapperTransactionReceipt transactionReceipt, String address, List<Object> values) {
        return new CheckedEvent(definition, transactionReceipt, (Boolean) values.get(0), address);
    }
}
