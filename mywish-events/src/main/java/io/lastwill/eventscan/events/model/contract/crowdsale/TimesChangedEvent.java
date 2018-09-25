package io.lastwill.eventscan.events.model.contract.crowdsale;

import io.mywish.blockchain.ContractEvent;
import io.mywish.blockchain.ContractEventDefinition;
import lombok.Getter;

import java.math.BigInteger;

@Getter
public class TimesChangedEvent extends ContractEvent {
    private final BigInteger startTime;
    private final BigInteger endTime;

    public TimesChangedEvent(ContractEventDefinition definition, String address, BigInteger startTime, BigInteger endTime) {
        super(definition, address);
        this.startTime = startTime;
        this.endTime = endTime;
    }
}
