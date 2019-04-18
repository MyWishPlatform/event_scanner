package io.mywish.web3.blockchain.builders.swaps2;

import io.lastwill.eventscan.events.model.contract.swaps2.SwapEvent;
import io.mywish.web3.blockchain.builders.Web3ContractEventBuilder;
import io.mywish.web3.blockchain.model.Web3ContractEventDefinition;
import io.mywish.web3.blockchain.model.WrapperType;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.stereotype.Component;
import org.web3j.abi.TypeEncoder;
import org.web3j.abi.datatypes.Address;
import org.web3j.abi.datatypes.generated.Bytes32;

import java.util.Arrays;
import java.util.List;

@Getter
@Component
@NoArgsConstructor
public class OrderSwappedEventBuilder extends Web3ContractEventBuilder<SwapEvent> {
    private final Web3ContractEventDefinition definition = new Web3ContractEventDefinition(
            "OrderSwapped",
            Arrays.asList(
                    WrapperType.create(Bytes32.class, false),
                    WrapperType.create(Address.class, true)
            )
    );

    @Override
    public SwapEvent build(String address, List<Object> values) {
        return new SwapEvent(
                definition,
                TypeEncoder.encode((Bytes32) values.get(0)),
                (String) values.get(1),
                address
        );
    }
}
