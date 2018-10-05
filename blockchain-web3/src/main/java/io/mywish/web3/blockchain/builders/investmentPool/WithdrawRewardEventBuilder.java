package io.mywish.web3.blockchain.builders.investmentPool;

import io.lastwill.eventscan.events.model.contract.investmentPool.WithdrawRewardEvent;
import io.mywish.web3.blockchain.builders.Web3ContractEventBuilder;
import io.mywish.web3.blockchain.model.Web3ContractEventDefinition;
import io.mywish.web3.blockchain.model.WrapperType;
import lombok.NoArgsConstructor;
import org.springframework.stereotype.Component;
import org.web3j.abi.datatypes.Address;
import org.web3j.abi.datatypes.Uint;

import java.math.BigInteger;
import java.util.Arrays;
import java.util.List;

@Component
@NoArgsConstructor
public class WithdrawRewardEventBuilder extends Web3ContractEventBuilder<WithdrawRewardEvent> {
    private final static Web3ContractEventDefinition definition = new Web3ContractEventDefinition(
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
    public Web3ContractEventDefinition getDefinition() {
        return definition;
    }
}
