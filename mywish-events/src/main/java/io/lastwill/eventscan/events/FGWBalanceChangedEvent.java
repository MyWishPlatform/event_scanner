package io.lastwill.eventscan.events;

import io.lastwill.eventscan.model.CryptoCurrency;
import io.mywish.scanner.model.BaseEvent;
import io.mywish.scanner.model.NetworkType;
import lombok.Getter;

import java.math.BigInteger;

/**
 * Federation gateway address balance changed event
 */
@Getter
public class FGWBalanceChangedEvent extends BaseEvent {
    private final String address;
    private final BigInteger actualBalance;
    private final BigInteger delta;
    private final CryptoCurrency currency;
    private final long blockNo;

    public FGWBalanceChangedEvent(NetworkType networkType, String address, BigInteger actualBalance, BigInteger delta, CryptoCurrency currency, long blockNo) {
        super(networkType);
        this.address = address;
        this.actualBalance = actualBalance;
        this.delta = delta;
        this.currency = currency;
        this.blockNo = blockNo;
    }
}
