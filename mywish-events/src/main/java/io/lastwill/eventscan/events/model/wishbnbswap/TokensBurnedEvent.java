package io.lastwill.eventscan.events.model.wishbnbswap;

import io.lastwill.eventscan.events.model.BaseEvent;
import io.lastwill.eventscan.model.NetworkType;
import io.lastwill.eventscan.model.EthToBnbSwapEntry;
import lombok.Getter;

@Getter
public class TokensBurnedEvent extends BaseEvent {
    private final String coin;
    private final int decimals;
    private final EthToBnbSwapEntry swapEntry;
    private final String ethAddress;
    private final String bnbAddress;

    public TokensBurnedEvent(
            String coin,
            int decimals,
            EthToBnbSwapEntry swapEntry,
            String ethAddress,
            String bnbAddress
    ) {
        super(NetworkType.ETHEREUM_MAINNET);
        this.coin = coin;
        this.decimals = decimals;
        this.swapEntry = swapEntry;
        this.ethAddress = ethAddress;
        this.bnbAddress = bnbAddress;
    }
}
