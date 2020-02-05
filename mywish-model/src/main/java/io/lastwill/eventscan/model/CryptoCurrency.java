package io.lastwill.eventscan.model;

import lombok.Getter;

@Getter
public enum CryptoCurrency {
    ETH(18),
    WISH(18),
    DUCX(18),
    BTC(8),
    DUC(8),
    RSK(0),
    NEO(4),
    GAS(8),
    EOS(4),
    EOSISH(4),
    BNB(18),
    SWAP(18),
    TRX(6),
    TRONISH(6),
    BBNB(8),
    BWISH(8),
    USD(6, "$");

    private final int decimals;
    private final String symbol;

    CryptoCurrency(int decimals, String symbol) {
        this.decimals = decimals;
        this.symbol = symbol;
    }

    CryptoCurrency(int decimals) {
        this(decimals, null);
    }

    @Override
    public String toString() {
        return symbol != null
                ? symbol
                : super.toString();
    }
}
