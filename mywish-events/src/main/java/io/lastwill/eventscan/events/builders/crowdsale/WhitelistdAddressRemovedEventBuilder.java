package io.lastwill.eventscan.events.builders.crowdsale;

import io.lastwill.eventscan.events.model.contract.crowdsale.WhitelistedAddressRemovedEvent;
import io.mywish.wrapper.ContractEventDefinition;
import io.mywish.wrapper.WrapperTransactionReceipt;
import org.springframework.stereotype.Component;

@Component
public class WhitelistdAddressRemovedEventBuilder extends WhitelistdEventBuilder<WhitelistedAddressRemovedEvent> {
    public WhitelistdAddressRemovedEventBuilder() {
        super("WhitelistedAddressRemoved");
    }

    protected WhitelistedAddressRemovedEvent buildInner(final ContractEventDefinition definition, final WrapperTransactionReceipt transactionReceipt, String address, String whitelistedAddress) {
        return new WhitelistedAddressRemovedEvent(definition, transactionReceipt, address, whitelistedAddress);
    }
}
