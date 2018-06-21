package io.lastwill.eventscan.events.builders.crowdsale;

import io.lastwill.eventscan.events.model.contract.crowdsale.WhitelistedEvent;
import io.mywish.wrapper.ContractEventBuilder;
import io.mywish.wrapper.ContractEventDefinition;
import io.mywish.wrapper.WrapperTransactionReceipt;
import io.mywish.wrapper.WrapperType;
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
    public T build(WrapperTransactionReceipt transactionReceipt, String address, List<Object> values) {
        return buildInner(definition, transactionReceipt, address, (String) values.get(0));
    }

    protected abstract T buildInner(final ContractEventDefinition definition, final WrapperTransactionReceipt transactionReceipt, String address, String whitelistedAddress);
}
