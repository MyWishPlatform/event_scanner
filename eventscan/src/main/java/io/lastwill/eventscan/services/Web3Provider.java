package io.lastwill.eventscan.services;

import io.mywish.scanner.model.NetworkType;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.web3j.protocol.Web3j;
import javax.annotation.PostConstruct;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@Component
public class Web3Provider {
    @Autowired
    private Map<String, Web3j> web3ByNetwork = new HashMap<>();

    private final Map<NetworkType, Web3j> web3ByNetworkType = new HashMap<>();

    @PostConstruct
    protected void init() {
        web3ByNetwork.keySet()
                .forEach(network -> {
                    NetworkType networkType = NetworkType.valueOf(network);
                    web3ByNetworkType.put(networkType, web3ByNetwork.get(network));
                });
    }

    public Web3j get(NetworkType networkType) {
        Web3j web3j = web3ByNetworkType.get(networkType);
        if (web3j == null) {
            throw new UnsupportedOperationException("There is no web3 for " + networkType);
        }
        return web3j;
    }

    public Collection<NetworkType> getAvailableNetworkTypes() {
        return web3ByNetworkType.keySet();
    }
}
