package io.lastwill.eventscan.events.model.wishbnbswap;

import io.lastwill.eventscan.events.model.BaseEvent;
import io.lastwill.eventscan.model.NetworkType;
import io.lastwill.eventscan.model.WishToBnbSwapEntry;
import lombok.Getter;

import java.math.BigInteger;

@Getter
public class LowBalanceEvent extends BaseEvent {
    private final String coin;
    private final int decimals;
    private final WishToBnbSwapEntry swapEntry;
    private final BigInteger need;
    private final BigInteger have;

    public LowBalanceEvent(String coin, int decimals, WishToBnbSwapEntry swapEntry, BigInteger need, BigInteger have) {
        super(NetworkType.BINANCE_MAINNET);
        this.coin = coin;
        this.decimals = decimals;
        this.swapEntry = swapEntry;
        this.need = need;
        this.have = have;
    }
}
