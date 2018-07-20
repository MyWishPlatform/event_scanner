package io.lastwill.eventscan.events.builders.investmentPool;

import io.lastwill.eventscan.events.model.contract.investmentPool.InvestEvent;
import io.mywish.wrapper.ContractEventBuilder;
import io.mywish.wrapper.ContractEventDefinition;
import io.mywish.wrapper.WrapperType;
import org.springframework.stereotype.Component;
import org.web3j.abi.datatypes.Address;
import org.web3j.abi.datatypes.Uint;

import java.math.BigInteger;
import java.util.Arrays;
import java.util.List;

@Component
public class InvestEventBuilder extends ContractEventBuilder<InvestEvent> {
    private final static ContractEventDefinition definition = new ContractEventDefinition(
            "Invest",
            Arrays.asList(
                    WrapperType.create(Address.class, true),
                    WrapperType.create(Uint.class, false)
            )
    );

    @Override
    public InvestEvent build(String address, List<Object> values) {
        return new InvestEvent(
                definition,
                address,
                (String) values.get(0),
                (BigInteger) values.get(1)
        );
    }

    @Override
    public ContractEventDefinition getDefinition() {
        return definition;
    }
}
