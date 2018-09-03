package io.lastwill.eventscan.events.builders.crowdsale;

import io.lastwill.eventscan.events.model.contract.crowdsale.TimesChangedEvent;
import io.mywish.wrapper.ContractEventBuilder;
import io.mywish.wrapper.ContractEventDefinition;
import io.mywish.wrapper.WrapperType;
import lombok.Getter;
import org.springframework.stereotype.Component;
import org.web3j.abi.datatypes.Uint;

import java.math.BigInteger;
import java.util.Collections;
import java.util.List;

@Getter
@Component
public class SetFinishEventBuilder extends ContractEventBuilder<TimesChangedEvent> {
    private final ContractEventDefinition definition = new ContractEventDefinition(
            "setfinish",
            Collections.singletonList(
                    WrapperType.create(Uint.class, false)
            )
    );

    @Override
    public TimesChangedEvent build(String address, List<Object> values) {
        return new TimesChangedEvent(
                definition,
                address,
                null,
                (BigInteger) values.get(0)
        );
    }
}
