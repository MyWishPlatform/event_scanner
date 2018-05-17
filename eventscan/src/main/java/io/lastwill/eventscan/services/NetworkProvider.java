package io.lastwill.eventscan.services;

import io.mywish.scanner.Network;
import io.mywish.scanner.model.NetworkType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@Component
public class NetworkProvider {
    @Autowired
    private Map<String, Network> networkByName = new HashMap<>();

    private final Map<NetworkType, Network> networkByType = new HashMap<>();

    @PostConstruct
    protected void init() {
        networkByName.keySet().forEach(network -> {
            networkByType.put(NetworkType.valueOf(network), networkByName.get(network));
        });
    }

    public Network get(NetworkType networkType) {
        Network network = networkByType.get(networkType);
        if (network == null) {
            throw new UnsupportedOperationException("There is no Network for " + networkType);
        }
        return network;
    }

    public Collection<NetworkType> getAvailableNetworkTypes() {
        return networkByType.keySet();
    }
}
