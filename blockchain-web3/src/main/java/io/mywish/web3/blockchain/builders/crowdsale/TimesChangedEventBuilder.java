package io.mywish.web3.blockchain.builders.crowdsale;

import io.lastwill.eventscan.events.model.contract.crowdsale.TimesChangedEvent;
import io.mywish.blockchain.ContractEventDefinition;
import io.mywish.web3.blockchain.builders.Web3ContractEventBuilder;
import io.mywish.web3.blockchain.model.Web3ContractEventDefinition;
import io.mywish.web3.blockchain.model.WrapperType;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.stereotype.Component;
import org.web3j.abi.datatypes.Uint;

import java.math.BigInteger;
import java.util.Arrays;
import java.util.List;

@Getter
@Component
@NoArgsConstructor
public class TimesChangedEventBuilder extends Web3ContractEventBuilder<TimesChangedEvent> {
    private final Web3ContractEventDefinition definition = new Web3ContractEventDefinition(
            "TimesChanged",
            Arrays.asList(
                    WrapperType.create(Uint.class, false),
                    WrapperType.create(Uint.class, false),
                    WrapperType.create(Uint.class, false),
                    WrapperType.create(Uint.class, false)
            )
    );

    @Override
    public TimesChangedEvent build(String address, List<Object> values) {
        return new TimesChangedEvent(
                definition,
                address,
                (BigInteger) values.get(0),
                (BigInteger) values.get(1)
        );
    }
}
