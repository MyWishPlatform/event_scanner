package io.mywish.web3.blockchain.builders.swaps;

import io.lastwill.eventscan.events.model.contract.swaps.SwapEvent;
import io.mywish.web3.blockchain.builders.Web3ContractEventBuilder;
import io.mywish.web3.blockchain.model.Web3ContractEventDefinition;
import io.mywish.web3.blockchain.model.WrapperType;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.stereotype.Component;
import org.web3j.abi.datatypes.Address;

import java.util.Collections;
import java.util.List;

@Getter
@Component
@NoArgsConstructor
public class SwapEventBuilder extends Web3ContractEventBuilder<SwapEvent> {
    private final Web3ContractEventDefinition definition = new Web3ContractEventDefinition(
            "Swap",
            Collections.singletonList(WrapperType.create(Address.class, true))
    );


    @Override
    public SwapEvent build(String address, List<Object> values) {
        return new SwapEvent(
                definition,
                (String) values.get(0),
                address
        );
    }
}
