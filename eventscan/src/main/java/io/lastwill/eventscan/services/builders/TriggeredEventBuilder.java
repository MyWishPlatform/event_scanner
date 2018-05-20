package io.lastwill.eventscan.services.builders;

import io.mywish.wrapper.ContractEventBuilder;
import io.mywish.wrapper.WrapperTransactionReceipt;
import io.mywish.wrapper.WrapperType;
import io.mywish.wrapper.ContractEventDefinition;
import io.lastwill.eventscan.events.contract.TriggeredEvent;
import lombok.Getter;
import org.springframework.stereotype.Component;
import org.web3j.abi.datatypes.Uint;
import java.math.BigInteger;
import java.util.Arrays;
import java.util.List;

@Getter
@Component
public class TriggeredEventBuilder extends ContractEventBuilder<TriggeredEvent> {
    private final ContractEventDefinition definition = new ContractEventDefinition(
            "Triggered",
            Arrays.asList(WrapperType.create(Uint.class, false))
    );

    @Override
    public TriggeredEvent build(WrapperTransactionReceipt transactionReceipt, String address, List<Object> values) {
        return new TriggeredEvent(definition, transactionReceipt, (BigInteger) values.get(0), address);
    }
}
