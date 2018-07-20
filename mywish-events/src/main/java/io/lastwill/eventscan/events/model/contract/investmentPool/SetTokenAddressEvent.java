package io.lastwill.eventscan.events.model.contract.investmentPool;

import io.mywish.wrapper.ContractEvent;
import io.mywish.wrapper.ContractEventDefinition;
import lombok.Getter;

@Getter
public class SetTokenAddressEvent extends ContractEvent {
    private final String tokenAddress;
    public SetTokenAddressEvent(ContractEventDefinition definition, String address, String tokenAddress) {
        super(definition, address);
        this.tokenAddress = tokenAddress;
    }
}
