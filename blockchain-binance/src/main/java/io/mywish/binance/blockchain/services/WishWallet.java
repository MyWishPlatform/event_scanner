package io.mywish.binance.blockchain.services;

import com.binance.dex.api.client.BinanceDexEnvironment;
import io.lastwill.eventscan.model.CryptoCurrency;
import io.mywish.binance.blockchain.model.WrapperWallet;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class WishWallet extends WrapperWallet {

    public WishWallet(@Value("${io.lastwill.eventscan.binance.wish-swap.wallet-pk}") String privateKey,
                      BinanceDexEnvironment env) {
        super(CryptoCurrency.BWISH, privateKey, env);
    }
}
