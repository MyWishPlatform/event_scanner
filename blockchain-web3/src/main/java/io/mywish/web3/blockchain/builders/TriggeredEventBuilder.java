package io.mywish.web3.blockchain.builders;

import io.lastwill.eventscan.events.TypeHelper;
import io.lastwill.eventscan.events.model.contract.TriggeredEvent;
import io.lastwill.eventscan.model.NetworkProviderType;
import io.mywish.blockchain.ContractEventDefinition;
import io.mywish.web3.blockchain.model.Web3ContractEventDefinition;
import io.mywish.web3.blockchain.model.WrapperType;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.web3j.abi.datatypes.Uint;

import java.util.Collections;
import java.util.List;

@Getter
@Component
@NoArgsConstructor
public class TriggeredEventBuilder extends Web3ContractEventBuilder<TriggeredEvent> {
    @Autowired
    private TypeHelper typeHelper;

    private final Web3ContractEventDefinition definition = new Web3ContractEventDefinition(
            "Triggered",
            Collections.singletonList(WrapperType.create(Uint.class, false))
    );

    @Override
    public TriggeredEvent build(String address, List<Object> values) {
        return new TriggeredEvent(definition, typeHelper.toBigInteger(values.get(0)), address);
    }
}
