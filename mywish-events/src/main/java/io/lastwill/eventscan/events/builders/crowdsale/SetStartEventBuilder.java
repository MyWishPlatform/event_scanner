package io.lastwill.eventscan.events.builders.crowdsale;

import io.lastwill.eventscan.events.model.contract.crowdsale.TimesChangedEvent;
import io.mywish.blockchain.ContractEventBuilder;
import io.mywish.blockchain.ContractEventDefinition;
import io.mywish.blockchain.WrapperType;
import lombok.Getter;
import org.springframework.stereotype.Component;
import org.web3j.abi.datatypes.Uint;

import java.math.BigInteger;
import java.util.Collections;
import java.util.List;

@Getter
@Component
public class SetStartEventBuilder extends ContractEventBuilder<TimesChangedEvent> {
    private final ContractEventDefinition definition = new ContractEventDefinition(
            "setstart",
            Collections.singletonList(
                    WrapperType.create(Uint.class, false)
            )
    );

    @Override
    public TimesChangedEvent build(String address, List<Object> values) {
        return new TimesChangedEvent(
                definition,
                address,
                (BigInteger) values.get(0),
                null
        );
    }
}
