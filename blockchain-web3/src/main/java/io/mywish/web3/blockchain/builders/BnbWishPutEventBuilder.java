package io.mywish.web3.blockchain.builders;

import io.lastwill.eventscan.events.model.contract.BnbWishPutEvent;
import io.mywish.web3.blockchain.model.Web3ContractEventDefinition;
import io.mywish.web3.blockchain.model.WrapperType;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.stereotype.Component;
import org.web3j.abi.datatypes.Address;
import org.web3j.abi.datatypes.Utf8String;

import java.util.Arrays;
import java.util.List;

@Getter
@Component
@NoArgsConstructor
public class BnbWishPutEventBuilder extends Web3ContractEventBuilder<BnbWishPutEvent> {
    private final Web3ContractEventDefinition definition = new Web3ContractEventDefinition(
            "Put",
            Arrays.asList(
                    WrapperType.create(Address.class, true),
                    WrapperType.create(Utf8String.class, true)
            )
    );

    @Override
    public BnbWishPutEvent build(String address, List<Object> values) {
        return new BnbWishPutEvent(definition, (String) values.get(0), (String) values.get(1), address);
    }
}
