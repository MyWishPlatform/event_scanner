package io.mywish.web3.blockchain.builders.swaps2;

import io.lastwill.eventscan.events.model.contract.swaps2.CancelEvent;
import io.mywish.web3.blockchain.builders.Web3ContractEventBuilder;
import io.mywish.web3.blockchain.model.Web3ContractEventDefinition;
import io.mywish.web3.blockchain.model.WrapperType;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.stereotype.Component;
import org.web3j.abi.TypeEncoder;
import org.web3j.abi.datatypes.generated.Bytes32;

import java.util.Collections;
import java.util.List;

@Getter
@Component
@NoArgsConstructor
public class OrderCancelledEventBuilder extends Web3ContractEventBuilder<CancelEvent> {
    private final Web3ContractEventDefinition definition = new Web3ContractEventDefinition(
            "OrderCancelled",
            Collections.singletonList(WrapperType.create(Bytes32.class, false))
    );

    @Override
    public CancelEvent build(String address, List<Object> values) {
        return new CancelEvent(
                definition,
                "0x" + TypeEncoder.encode(new Bytes32((byte[]) values.get(0))),
                address
        );
    }
}
