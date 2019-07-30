package io.lastwill.eventscan.model;

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

    public EthBnbProfile getProfileByEthLinkAddress(String linkAddress) {
        return ethBnbProfiles
                .stream()
                .filter(e -> e.getEthLinkAddress().equals(linkAddress))
                .findFirst()
                .orElseThrow(() -> new NoSuchElementException("No matching elements"));
    }

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
                .filter(e -> e.getEth().name().equals(ethSymbol))
                .findFirst()
                .orElseThrow(() -> new NoSuchElementException("No matching elements"));
    }
}
