package io.lastwill.eventscan.events.builders.crowdsale;

import io.lastwill.eventscan.events.model.contract.crowdsale.WhitelistedEvent;
import io.mywish.blockchain.ContractEventBuilder;
import io.mywish.blockchain.ContractEventDefinition;
import io.mywish.blockchain.WrapperType;
import lombok.Getter;
import org.web3j.abi.datatypes.Address;

import java.util.Collections;
import java.util.List;

public abstract class WhitelistdEventBuilder<T extends WhitelistedEvent> extends ContractEventBuilder<T> {
    @Getter
    private final ContractEventDefinition definition;

    public WhitelistdEventBuilder(final String name) {
        definition = new ContractEventDefinition(
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
