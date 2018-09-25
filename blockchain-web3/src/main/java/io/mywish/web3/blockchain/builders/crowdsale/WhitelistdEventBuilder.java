package io.mywish.web3.blockchain.builders.crowdsale;

import io.lastwill.eventscan.events.model.contract.crowdsale.WhitelistedEvent;
import io.mywish.blockchain.ContractEventDefinition;
import io.mywish.web3.blockchain.builders.Web3ContractEventBuilder;
import io.mywish.web3.blockchain.model.Web3ContractEventDefinition;
import io.mywish.web3.blockchain.model.WrapperType;
import lombok.Getter;
import org.web3j.abi.datatypes.Address;

import java.util.Collections;
import java.util.List;

public abstract class WhitelistdEventBuilder<T extends WhitelistedEvent> extends Web3ContractEventBuilder<T> {
    @Getter
    private final Web3ContractEventDefinition definition;

    public WhitelistdEventBuilder(final String name) {
        definition = new Web3ContractEventDefinition(
                name,
                Collections.singletonList(
                        WrapperType.create(Address.class, true)
                )
        );
    }

    @Override
    public T build(String address, List<Object> values) {
        return buildInner(definition, address, (String) values.get(0));
    }

    protected abstract T buildInner(final ContractEventDefinition definition, String address, String whitelistedAddress);
}
