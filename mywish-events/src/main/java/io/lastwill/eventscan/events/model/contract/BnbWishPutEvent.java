package io.lastwill.eventscan.events.model.contract;

import io.mywish.blockchain.ContractEvent;
import io.mywish.blockchain.ContractEventDefinition;
import lombok.Getter;

@Getter
public class BnbWishPutEvent extends ContractEvent {
    private final String eth;
    private final String bnb;

    public BnbWishPutEvent(final ContractEventDefinition definition, String eth, String bnb, String address) {
        super(definition, address);
        this.eth = eth;
        this.bnb = bnb;
    }
}
