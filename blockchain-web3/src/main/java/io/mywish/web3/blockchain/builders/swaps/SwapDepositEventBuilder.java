package io.mywish.web3.blockchain.builders.swaps;

import io.lastwill.eventscan.events.model.contract.swaps.DepositSwapEvent;
import io.mywish.web3.blockchain.builders.Web3ContractEventBuilder;
import io.mywish.web3.blockchain.model.Web3ContractEventDefinition;
import io.mywish.web3.blockchain.model.WrapperType;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.stereotype.Component;
import org.web3j.abi.datatypes.Address;
import org.web3j.abi.datatypes.Uint;

import java.math.BigInteger;
import java.util.Arrays;
import java.util.List;

@Getter
@Component
@NoArgsConstructor
public class SwapDepositEventBuilder extends Web3ContractEventBuilder<DepositSwapEvent> {
    private final Web3ContractEventDefinition definition = new Web3ContractEventDefinition(
            "Deposit",
            Arrays.asList(
                    WrapperType.create(Address.class, false),
                    WrapperType.create(Address.class, false),
                    WrapperType.create(Uint.class, false),
                    WrapperType.create(Uint.class, false)
            )
    );

    @Override
    public DepositSwapEvent build(String address, List<Object> values) {
        return new DepositSwapEvent(
                definition,
                address,
                (String) values.get(0),
                (String) values.get(1),
                (BigInteger) values.get(2),
                (BigInteger) values.get(3)
        );
    }
}