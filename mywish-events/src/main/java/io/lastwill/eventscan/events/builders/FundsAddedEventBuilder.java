package io.lastwill.eventscan.events.builders;

import io.mywish.wrapper.ContractEventBuilder;
import io.mywish.wrapper.ContractEventDefinition;
import io.lastwill.eventscan.events.model.contract.FundsAddedEvent;
import io.mywish.wrapper.WrapperType;
import lombok.Getter;
import org.springframework.stereotype.Component;
import org.web3j.abi.datatypes.Address;
import org.web3j.abi.datatypes.Uint;
import java.math.BigInteger;
import java.util.Arrays;
import java.util.List;

@Getter
@Component
public class FundsAddedEventBuilder extends ContractEventBuilder<FundsAddedEvent> {
    private final ContractEventDefinition definition = new ContractEventDefinition(
            "FundsAdded",
            Arrays.asList(
                    WrapperType.create(Address.class, true),
                    WrapperType.create(Uint.class, false)
            )
    );

    @Override
    public FundsAddedEvent build(String address, List<Object> values) {
        return new FundsAddedEvent(
                definition,
                (String) values.get(0),
                (BigInteger) values.get(1),
                address
        );
    }
}
