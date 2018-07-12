package io.lastwill.eventscan.events.model.contract.investmentPool;

import io.mywish.wrapper.ContractEvent;
import io.mywish.wrapper.ContractEventDefinition;
import lombok.Getter;

@Getter
public class SetInvestmentAddressEvent extends ContractEvent {
    private final String investmentAddress;
    public SetInvestmentAddressEvent(ContractEventDefinition definition, String address, String investmentAddress) {
        super(definition, address);
        this.investmentAddress = investmentAddress;
    }
}
