package io.lastwill.eventscan.events.builders.investmentPool;

import io.lastwill.eventscan.events.model.contract.investmentPool.WithdrawRewardEvent;
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
public class WithdrawRewardEventBuilder extends ContractEventBuilder<WithdrawRewardEvent> {
    private final static ContractEventDefinition definition = new ContractEventDefinition(
            "WithdrawReward",
            Arrays.asList(
                    WrapperType.create(Address.class, true),
                    WrapperType.create(Uint.class, false)
            )
    );

    @Override
    public WithdrawRewardEvent build(String address, List<Object> values) {
        return new WithdrawRewardEvent(
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
