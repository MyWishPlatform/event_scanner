package io.lastwill.eventscan.events.model.wishbnbswap;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Getter
@Component
public class LinkerAddressByCoin {

    @Value("${io.lastwill.eventscan.binance.wish-swap.linker-address}")
    private String wishAddress;

    private Map<String, SwapsCoin> addressByCoin = new HashMap<>();

    public enum SwapsCoin {
        WISH;
    }

     public LinkerAddressByCoin() {
        addressByCoin.put(wishAddress, SwapsCoin.WISH);

    }

}
