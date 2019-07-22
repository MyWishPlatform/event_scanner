package io.mywish.binance.blockchain.model;

import com.binance.dex.api.client.BinanceDexEnvironment;
import com.binance.dex.api.client.Wallet;
import io.lastwill.eventscan.model.CryptoCurrency;
import lombok.Getter;

@Getter
public abstract class WrapperWallet {
    private Wallet wallet;
    private CryptoCurrency symbol;


    public WrapperWallet(CryptoCurrency symbol, String privateKey, BinanceDexEnvironment env) {
        this.wallet = new Wallet(privateKey, env);
        this.symbol = symbol;
    }
}
