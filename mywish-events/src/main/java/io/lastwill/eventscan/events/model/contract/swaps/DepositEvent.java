package io.lastwill.eventscan.events.model.contract.swaps;

import io.mywish.blockchain.ContractEvent;
import io.mywish.blockchain.ContractEventDefinition;
import lombok.Getter;

import java.math.BigInteger;

@Getter
public class DepositEvent extends ContractEvent {
    private final String token;
    private final String user;
    private final BigInteger amount;
    private final BigInteger balance;

    public DepositEvent(ContractEventDefinition definition,
                        String address,
                        String token,
                        String user,
                        BigInteger amount,
                        BigInteger balance
    ) {
        super(definition, address);
        this.token = token;
        this.user = user;
        this.amount = amount;
        this.balance = balance;
    }
}
