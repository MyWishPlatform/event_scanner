package io.lastwill.eventscan.events.model.contract.crowdsale;

import io.mywish.wrapper.ContractEvent;
import io.mywish.wrapper.ContractEventDefinition;
import io.mywish.wrapper.WrapperTransactionReceipt;
import lombok.Getter;

import java.math.BigInteger;

@Getter
public class TimesChangedEvent extends ContractEvent {
    private final BigInteger startTime;
    private final BigInteger endTime;

    public TimesChangedEvent(ContractEventDefinition definition, String address, WrapperTransactionReceipt transactionReceipt, BigInteger startTime, BigInteger endTime) {
        super(definition, address, transactionReceipt);
        this.startTime = startTime;
        this.endTime = endTime;
    }
}
