package io.lastwill.eventscan.events.model.wishbnbswap;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.NoSuchElementException;

@Component
@Getter
public class ProfileStorage {

    @Autowired
    private List<EthBnbProfile> ethBnbProfiles;

    public EthBnbProfile getProfileByEthTokenAddress(String tokenAddress) {
        return ethBnbProfiles
                .stream()
                .filter(e -> e.getEthTokenAddress().equals(tokenAddress))
                .findFirst()
                .orElseThrow(() -> new NoSuchElementException("No matching elements"));
    }
    public EthBnbProfile getProfileByEthSymbol(String ethSymbol) {
        return ethBnbProfiles
                .stream()
                .filter(e -> e.getEth().getSymbol().equals(ethSymbol))
                .findFirst()
                .orElseThrow(() -> new NoSuchElementException("No matching elements"));
    }
}
