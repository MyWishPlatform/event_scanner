package io.mywish.web3.blockchain.builders.tokenProtector;

import io.lastwill.eventscan.events.model.contract.tokenProtector.SelfdestructionEvent;
import io.mywish.web3.blockchain.builders.Web3ContractEventBuilder;
import io.mywish.web3.blockchain.model.Web3ContractEventDefinition;
import io.mywish.web3.blockchain.model.WrapperType;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.stereotype.Component;
import org.web3j.abi.datatypes.Bool;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

@Component
@Getter
@NoArgsConstructor
public class SelfdestructionEventBuilder extends Web3ContractEventBuilder<SelfdestructionEvent> {

    private final Web3ContractEventDefinition definition = new Web3ContractEventDefinition(
            "SelfdestructionEvent",
            Collections.singletonList(
                    WrapperType.create(Bool.class, false)
            )
    );

    @Override
    public SelfdestructionEvent build(String address, List<Object> values) {
        return new SelfdestructionEvent(
                definition,
                address,
                (boolean) values.get(0)
        );
    }

}
