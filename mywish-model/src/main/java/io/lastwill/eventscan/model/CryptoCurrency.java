package io.lastwill.eventscan.model;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public enum CryptoCurrency {
    ETH(18),
    WISH(18),
    BTC(8),
    RSK(0),
    NEO(4),
    GAS(8),
    EOS(4),
    EOSISH(4),
    BNB(18),
    TRX(6),
    TRONISH(6);

    @Getter
    private final int decimals;
}
