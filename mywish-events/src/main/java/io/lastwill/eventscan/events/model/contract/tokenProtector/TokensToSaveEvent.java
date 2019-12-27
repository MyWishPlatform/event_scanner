package io.lastwill.eventscan.events.model.contract.tokenProtector;

import io.mywish.blockchain.ContractEvent;
import io.mywish.blockchain.ContractEventDefinition;
import lombok.Getter;

@Getter
public class TokensToSaveEvent extends ContractEvent {
    private final String tokenContract;

    public TokensToSaveEvent(ContractEventDefinition definition, String address, String tokenContract) {
        super(definition, address);
        this.tokenContract = tokenContract;
    }
}
