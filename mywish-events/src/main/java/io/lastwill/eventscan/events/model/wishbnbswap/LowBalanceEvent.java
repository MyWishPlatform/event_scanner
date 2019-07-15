package io.lastwill.eventscan.events.model.wishbnbswap;

import io.lastwill.eventscan.events.model.BaseEvent;
import io.lastwill.eventscan.model.NetworkType;
import io.lastwill.eventscan.model.EthToBnbSwapEntry;
import lombok.Getter;

import java.math.BigInteger;

@Getter
public class LowBalanceEvent extends BaseEvent {
    private final String coin;
    private final int decimals;
    private final EthToBnbSwapEntry swapEntry;
    private final BigInteger need;
    private final BigInteger have;
    private final String fromAddress;

    public LowBalanceEvent(String coin, int decimals, EthToBnbSwapEntry swapEntry, BigInteger need, BigInteger have, String fromAddress) {
        super(NetworkType.BINANCE_MAINNET);
        this.coin = coin;
        this.decimals = decimals;
        this.swapEntry = swapEntry;
        this.need = need;
        this.have = have;
        this.fromAddress = fromAddress;
    }
}
