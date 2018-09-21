package io.lastwill.eventscan.events.builders;

import io.mywish.blockchain.ContractEventBuilder;
import io.mywish.blockchain.WrapperType;
import io.mywish.blockchain.ContractEventDefinition;
import io.lastwill.eventscan.events.model.contract.KilledEvent;
import lombok.Getter;
import org.springframework.stereotype.Component;
import org.web3j.abi.datatypes.Bool;

import java.util.Collections;
import java.util.List;

@Getter
@Component
public class KilledEventBuilder extends ContractEventBuilder<KilledEvent> {
    private final ContractEventDefinition definition = new ContractEventDefinition(
            "Killed",
            Collections.singletonList(WrapperType.create(Bool.class, false))
    );

    @Override
    public KilledEvent build(String address, List<Object> values) {
        return new KilledEvent(
                definition,
                (Boolean) values.get(0),
                address
        );
    }
}
