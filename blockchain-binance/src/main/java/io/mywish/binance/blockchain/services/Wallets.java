package io.mywish.binance.blockchain.services;

import com.binance.dex.api.client.Wallet;
import io.lastwill.eventscan.model.CryptoCurrency;
import io.mywish.binance.blockchain.model.WrapperWallet;

import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class Wallets {

    private Map<CryptoCurrency, Wallet> walletStorage;


    public Wallets(List<WrapperWallet> wallets) {
        this.walletStorage = new HashMap<>();
        wallets.forEach(wallet -> this.walletStorage.put(wallet.getSymbol(), wallet.getWallet()));
    }

    public Wallet getWalletBySymbol(CryptoCurrency symbol) {
        return this.walletStorage.get(symbol);
    }
}
