package io.lastwill.eventscan.events.model.contract.swaps;

import io.mywish.blockchain.ContractEvent;
import io.mywish.blockchain.ContractEventDefinition;
import lombok.Getter;

import java.math.BigInteger;

@Getter
public class RefundSwapEvent extends ContractEvent {
    private final String token;
    private final String user;
    private final BigInteger amount;


    public RefundSwapEvent(ContractEventDefinition definition,
                           String address,
                           String token,
                           String user,
                           BigInteger amount
    ) {
        super(definition, address);
        this.token = token;
        this.user = user;
        this.amount = amount;

    }
}
