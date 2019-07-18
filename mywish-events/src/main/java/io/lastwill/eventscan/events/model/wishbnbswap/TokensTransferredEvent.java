package io.lastwill.eventscan.events.model.wishbnbswap;

import io.lastwill.eventscan.events.model.BaseEvent;
import io.lastwill.eventscan.model.NetworkType;
import io.lastwill.eventscan.model.EthToBnbSwapEntry;
import lombok.Getter;

@Getter
public class TokensTransferredEvent extends BaseEvent {
    private final String coin;
    private final int decimals;
    private final EthToBnbSwapEntry ethEntry;

    public TokensTransferredEvent(
            String coin,
            int decimals,
            EthToBnbSwapEntry ethEntry
    ) {
        super(NetworkType.BINANCE_MAINNET);
        this.coin = coin;
        this.decimals = decimals;
        this.ethEntry = ethEntry;
    }
}
