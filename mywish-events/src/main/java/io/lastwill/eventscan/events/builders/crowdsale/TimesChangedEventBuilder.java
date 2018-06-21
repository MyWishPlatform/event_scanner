package io.lastwill.eventscan.events.builders.crowdsale;

import io.lastwill.eventscan.events.model.contract.crowdsale.TimesChangedEvent;
import io.mywish.wrapper.ContractEventBuilder;
import io.mywish.wrapper.ContractEventDefinition;
import io.mywish.wrapper.WrapperTransactionReceipt;
import io.mywish.wrapper.WrapperType;
import lombok.Getter;
import org.springframework.stereotype.Component;
import org.web3j.abi.datatypes.Uint;

import java.math.BigInteger;
import java.util.Arrays;
import java.util.List;

@Getter
@Component
public class TimesChangedEventBuilder extends ContractEventBuilder<TimesChangedEvent> {
    private final ContractEventDefinition definition = new ContractEventDefinition(
            "TimesChanged",
            Arrays.asList(
                    WrapperType.create(Uint.class, false),
                    WrapperType.create(Uint.class, false),
                    WrapperType.create(Uint.class, false),
                    WrapperType.create(Uint.class, false)
            )
    );

    @Override
    public TimesChangedEvent build(WrapperTransactionReceipt transactionReceipt, String address, List<Object> values) {
        return new TimesChangedEvent(
                definition,
                address,
                transactionReceipt,
                (BigInteger) values.get(0),
                (BigInteger) values.get(1)
        );
    }
}
