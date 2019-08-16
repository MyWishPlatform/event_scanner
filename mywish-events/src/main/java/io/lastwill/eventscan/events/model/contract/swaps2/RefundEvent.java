package io.lastwill.eventscan.events.model.contract.swaps2;

import io.mywish.blockchain.ContractEventDefinition;
import lombok.Getter;

import java.math.BigInteger;

@Getter
public class RefundEvent extends Swaps2BaseEvent {
    private final String token;
    private final String user;
    private final BigInteger amount;

    public RefundEvent(
            ContractEventDefinition definition,
            String id,
            String token,
            String user,
            BigInteger amount,
            String address
    ) {
        super(definition, id, address);
        this.token = token;
        this.user = user;
        this.amount = amount;
    }
}
