package io.lastwill.eventscan.events.model.contract.swaps2;

import io.mywish.blockchain.ContractEvent;
import io.mywish.blockchain.ContractEventDefinition;
import lombok.Getter;

import java.math.BigInteger;

@Getter
public class OrderCreatedEvent extends Swaps2BaseEvent {
    private final String owner;
    private final String baseAddress;
    private final String quoteAddress;
    private final BigInteger baseLimit;
    private final BigInteger quoteLimit;
    private final BigInteger expirationTimestamp;
    private final String baseOnlyInvestor;
    private final BigInteger minBaseInvestment;
    private final BigInteger minQuoteInvestment;

    public OrderCreatedEvent(
            ContractEventDefinition definition,
            String id,
            String owner,
            String baseAddress,
            String quoteAddress,
            BigInteger baseLimit,
            BigInteger quoteLimit,
            BigInteger expirationTimestamp,
            String baseOnlyInvestor,
            BigInteger minBaseInvestment,
            BigInteger minQuoteInvestment,
            String address
    ) {
        super(definition, id, address);
        this.owner = owner;
        this.baseAddress = baseAddress;
        this.quoteAddress = quoteAddress;
        this.baseLimit = baseLimit;
        this.quoteLimit = quoteLimit;
        this.expirationTimestamp = expirationTimestamp;
        this.baseOnlyInvestor = baseOnlyInvestor;
        this.minBaseInvestment = minBaseInvestment;
        this.minQuoteInvestment = minQuoteInvestment;
    }
}
