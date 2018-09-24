package io.mywish.web3.blockchain.builders.investmentPool;

import io.lastwill.eventscan.events.model.contract.investmentPool.SetTokenAddressEvent;
import io.mywish.web3.blockchain.builders.Web3ContractEventBuilder;
import io.mywish.web3.blockchain.model.Web3ContractEventDefinition;
import io.mywish.web3.blockchain.model.WrapperType;
import lombok.NoArgsConstructor;
import org.springframework.stereotype.Component;
import org.web3j.abi.datatypes.Address;

import java.util.Collections;
import java.util.List;

@Component
@NoArgsConstructor
public class SetTokenAddressEventBuilder extends Web3ContractEventBuilder<SetTokenAddressEvent> {
    private final static Web3ContractEventDefinition definition = new Web3ContractEventDefinition(
            "SetTokenAddress",
            Collections.singletonList(
                    WrapperType.create(Address.class, true)
            )
    );

    @Override
    public SetTokenAddressEvent build(String address, List<Object> values) {
        return new SetTokenAddressEvent(
                definition,
                address,
                (String) values.get(0)
        );
    }

    @Override
    public Web3ContractEventDefinition getDefinition() {
        return definition;
    }
}
