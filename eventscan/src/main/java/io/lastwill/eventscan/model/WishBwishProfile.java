package io.lastwill.eventscan.model;

import io.lastwill.eventscan.services.Bep2WishSender;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class WishBwishProfile extends EthBnbProfile {

    @Autowired
    public WishBwishProfile(
            @Value(value = "${io.lastwill.eventscan.binance.wish-swap.linker-address}")String wishLinkAddress,
            @Value("${io.lastwill.eventscan.binance.wish-swap.burner-address}")String ethBurnerAddress,
            @Value("${io.lastwill.eventscan.contract.token-address.wish}")String ethTokenAddress,
            @Value("${io.lastwill.eventscan.binance-wish.token-symbol}")String transferSymbol,
            Bep2WishSender bep2WishSender) {
        super(wishLinkAddress, ethBurnerAddress, ethTokenAddress, transferSymbol, bep2WishSender, CryptoCurrency.WISH, CryptoCurrency.BWISH);
    }
}
