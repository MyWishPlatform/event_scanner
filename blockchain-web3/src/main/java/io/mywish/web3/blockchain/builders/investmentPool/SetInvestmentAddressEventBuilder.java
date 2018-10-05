package io.mywish.web3.blockchain.builders.investmentPool;

import io.lastwill.eventscan.events.model.contract.investmentPool.SetInvestmentAddressEvent;
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
public class SetInvestmentAddressEventBuilder extends Web3ContractEventBuilder<SetInvestmentAddressEvent> {
    private final static Web3ContractEventDefinition definition = new Web3ContractEventDefinition(
            "SetInvestmentAddress",
            Collections.singletonList(
                    WrapperType.create(Address.class, true)
            )
    );

    @Override
    public SetInvestmentAddressEvent build(String address, List<Object> values) {
        return new SetInvestmentAddressEvent(
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
