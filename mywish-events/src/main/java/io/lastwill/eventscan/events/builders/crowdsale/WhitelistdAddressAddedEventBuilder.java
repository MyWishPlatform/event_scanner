package io.lastwill.eventscan.events.builders.crowdsale;

import io.lastwill.eventscan.events.model.contract.crowdsale.WhitelistedAddressAddedEvent;
import io.lastwill.eventscan.events.model.contract.crowdsale.WhitelistedEvent;
import io.mywish.wrapper.ContractEventBuilder;
import io.mywish.wrapper.ContractEventDefinition;
import io.mywish.wrapper.WrapperTransactionReceipt;
import io.mywish.wrapper.WrapperType;
import org.springframework.stereotype.Component;
import org.web3j.abi.datatypes.Address;

import java.util.Collections;
import java.util.List;

@Component
public class WhitelistdAddressAddedEventBuilder extends WhitelistdEventBuilder<WhitelistedAddressAddedEvent> {
    public WhitelistdAddressAddedEventBuilder() {
        super("WhitelistedAddressAdded");
    }

    protected WhitelistedAddressAddedEvent buildInner(final ContractEventDefinition definition, final WrapperTransactionReceipt transactionReceipt, String address, String whitelistedAddress) {
        return new WhitelistedAddressAddedEvent(definition, transactionReceipt, address, whitelistedAddress);
    }
}
