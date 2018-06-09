package io.lastwill.eventscan.messages;

import lombok.Getter;

import java.math.BigInteger;

@Getter
public class TimesChangedNotify extends NotifyContract {
    private final BigInteger startTime;
    private final BigInteger endTime;

    public TimesChangedNotify(int contractId, String txHash, BigInteger startTime, BigInteger endTime) {
        super(contractId, PaymentStatus.COMMITTED, txHash);
        this.startTime = startTime;
        this.endTime = endTime;
    }

    @Override
    public String getType() {
        return "timesChanged";
    }
}
