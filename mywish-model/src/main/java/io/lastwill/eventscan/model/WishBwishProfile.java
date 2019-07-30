package io.lastwill.eventscan.model;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class WishBwishProfile extends EthBnbProfile {

    public WishBwishProfile(
            @Value(value = "${io.lastwill.eventscan.binance.wish-swap.linker-address}")String wishLinkAddress,
            @Value("${io.lastwill.eventscan.binance.wish-swap.burner-address}")String ethBurnerAddress,
            @Value("${io.lastwill.eventscan.contract.token-address.wish}")String ethTokenAddress,
            @Value("${io.lastwill.eventscan.binance-wish.token-symbol}")String transferSymbol) {
        super(wishLinkAddress, ethBurnerAddress, ethTokenAddress, transferSymbol, CryptoCurrency.WISH, CryptoCurrency.BWISH);
    }
}
