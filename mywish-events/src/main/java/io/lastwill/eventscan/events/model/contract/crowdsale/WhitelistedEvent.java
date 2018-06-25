package io.lastwill.eventscan.events.model.contract.crowdsale;

import io.mywish.wrapper.ContractEvent;
import io.mywish.wrapper.ContractEventDefinition;
import io.mywish.wrapper.WrapperTransactionReceipt;
import lombok.Getter;

@Getter
public abstract class WhitelistedEvent extends ContractEvent {
    private final String whitelistedAddress;

    public WhitelistedEvent(ContractEventDefinition definition, WrapperTransactionReceipt transactionReceipt, String address, String whitelistedAddress) {
        super(definition, address, transactionReceipt);
        this.whitelistedAddress = whitelistedAddress;
    }
}
