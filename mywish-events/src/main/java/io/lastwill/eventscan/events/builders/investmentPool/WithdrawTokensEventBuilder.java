package io.lastwill.eventscan.events.builders.investmentPool;

import io.lastwill.eventscan.events.model.contract.investmentPool.WithdrawTokensEvent;
import io.mywish.blockchain.ContractEventBuilder;
import io.mywish.blockchain.ContractEventDefinition;
import io.mywish.blockchain.WrapperType;
import org.springframework.stereotype.Component;
import org.web3j.abi.datatypes.Address;
import org.web3j.abi.datatypes.Uint;

import java.math.BigInteger;
import java.util.Arrays;
import java.util.List;

@Component
public class WithdrawTokensEventBuilder extends ContractEventBuilder<WithdrawTokensEvent> {
    private final static ContractEventDefinition definition = new ContractEventDefinition(
            "WithdrawTokens",
            Arrays.asList(
                    WrapperType.create(Address.class, true),
                    WrapperType.create(Uint.class, false)
            )
    );

    @Override
    public WithdrawTokensEvent build(String address, List<Object> values) {
        return new WithdrawTokensEvent(
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
