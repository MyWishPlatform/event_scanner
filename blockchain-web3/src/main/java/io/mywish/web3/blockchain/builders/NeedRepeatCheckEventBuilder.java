package io.mywish.web3.blockchain.builders;

import io.lastwill.eventscan.events.model.contract.CheckedEvent;
import io.lastwill.eventscan.model.NetworkProviderType;
import io.mywish.blockchain.ContractEventDefinition;
import io.mywish.web3.blockchain.model.Web3ContractEventDefinition;
import io.mywish.web3.blockchain.model.WrapperType;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.stereotype.Component;
import org.web3j.abi.datatypes.Bool;

import java.util.Collections;
import java.util.List;

@Getter
@Component
@NoArgsConstructor
public class NeedRepeatCheckEventBuilder extends Web3ContractEventBuilder<CheckedEvent> {
    private final Web3ContractEventDefinition definition = new Web3ContractEventDefinition(
            "NeedRepeatCheck",
            Collections.singletonList(WrapperType.create(Bool.class, false))
    );

    @Override
    public CheckedEvent build(String address, List<Object> values) {
        return new CheckedEvent(
                definition,
                (Boolean) values.get(0),
                address
        );
    }
}
