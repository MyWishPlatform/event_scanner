package io.mywish.web3.blockchain.builders.swaps;

import io.lastwill.eventscan.events.model.contract.swaps.RefundSwapEvent;
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
public class SwapRefundEventBuilder extends Web3ContractEventBuilder<RefundSwapEvent> {
    private final Web3ContractEventDefinition definition = new Web3ContractEventDefinition(
            "Refund",
            Arrays.asList(
                    WrapperType.create(Address.class, false),
                    WrapperType.create(Address.class, false),
                    WrapperType.create(Uint.class, false)
            )
    );

    @Override
    public RefundSwapEvent build(String address, List<Object> values) {
        return new RefundSwapEvent(
                definition,
                address,
                (String) values.get(0),
                (String) values.get(1),
                (BigInteger) values.get(2)
        );
    }
}