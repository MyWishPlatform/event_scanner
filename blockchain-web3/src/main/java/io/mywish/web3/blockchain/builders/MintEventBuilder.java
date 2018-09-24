package io.mywish.web3.blockchain.builders;

import io.lastwill.eventscan.events.model.contract.MintEvent;
import io.lastwill.eventscan.model.NetworkProviderType;
import io.mywish.blockchain.ContractEventDefinition;
import io.mywish.web3.blockchain.model.Web3ContractEventDefinition;
import io.mywish.web3.blockchain.model.WrapperType;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.stereotype.Component;
import org.web3j.abi.datatypes.Address;
import org.web3j.abi.datatypes.Uint;

import java.math.BigInteger;
import java.util.Arrays;
import java.util.List;

@Getter
@Component
@NoArgsConstructor
public class MintEventBuilder extends Web3ContractEventBuilder<MintEvent> {
    private final Web3ContractEventDefinition definition = new Web3ContractEventDefinition(
            "Mint",
            Arrays.asList(
                    WrapperType.create(Address.class, true),
                    WrapperType.create(Uint.class, false)
            )
    );

    @Override
    public MintEvent build(String address, List<Object> values) {
        return new MintEvent(definition, (String) values.get(0), (BigInteger) values.get(1), address);
    }
}
