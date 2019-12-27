package io.mywish.web3.blockchain.builders.tokenProtector;

import io.lastwill.eventscan.events.model.contract.tokenProtector.TokensToSaveEvent;
import io.mywish.web3.blockchain.builders.Web3ContractEventBuilder;
import io.mywish.web3.blockchain.model.Web3ContractEventDefinition;
import io.mywish.web3.blockchain.model.WrapperType;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.stereotype.Component;
import org.web3j.abi.datatypes.Address;

import java.util.Arrays;
import java.util.List;

@Component
@Getter
@NoArgsConstructor
public class TokensToSaveEventBuilder extends Web3ContractEventBuilder<TokensToSaveEvent> {

    private final Web3ContractEventDefinition definition = new Web3ContractEventDefinition(
            "TokensToSave",
            Arrays.asList(
                    WrapperType.create(Address.class, false)
            )
    );

    @Override
    public TokensToSaveEvent build(String address, List<Object> values) {
        return new TokensToSaveEvent(
                definition,
                address,
                (String) values.get(0)
        );
    }
}
