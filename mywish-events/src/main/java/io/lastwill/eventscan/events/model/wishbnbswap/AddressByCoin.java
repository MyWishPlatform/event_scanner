package io.lastwill.eventscan.events.model.wishbnbswap;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Getter
@Component
public class AddressByCoin {

    @Value("${io.lastwill.eventscan.binance.wish-swap.linker-address}")
    private String wishLinkAddress;

    @Value("${io.lastwill.eventscan.binance.wish-swap.burner-address}")
    private String wishBurnerAddress;

    @Value("${io.lastwill.eventscan.contract.token-address.wish}")
    private String wishTokenAddress;

    private Map<String, SwapsCoin> linkerAddressByCoin = new HashMap<>();
    private Map<SwapsCoin, String> burnerAddressByCoin = new HashMap<>();
    private Map<String, SwapsCoin> tokenAddressByCoin = new HashMap<>();

    public enum SwapsCoin {
        WISH;
    }

     public AddressByCoin() {
        linkerAddressByCoin.put(wishLinkAddress, SwapsCoin.WISH);
        burnerAddressByCoin.put(SwapsCoin.WISH, wishBurnerAddress);
        tokenAddressByCoin.put(wishTokenAddress, SwapsCoin.WISH);

    }

}
