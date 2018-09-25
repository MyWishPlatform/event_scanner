package io.lastwill.eventscan.services;

import io.lastwill.eventscan.model.NetworkType;
import io.mywish.blockchain.WrapperNetwork;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@Component
public class NetworkProvider {
    @Autowired
    private Map<String, WrapperNetwork> networkByName = new HashMap<>();

    private final Map<NetworkType, WrapperNetwork> networkByType = new HashMap<>();

    @PostConstruct
    protected void init() {
        networkByName.keySet().forEach(network -> {
            networkByType.put(NetworkType.valueOf(network), networkByName.get(network));
        });
    }

    public WrapperNetwork get(NetworkType networkType) {
        WrapperNetwork network = networkByType.get(networkType);
        if (network == null) {
            throw new UnsupportedOperationException("There is no Network for " + networkType);
        }
        return network;
    }

    public Collection<NetworkType> getAvailableNetworkTypes() {
        return networkByType.keySet();
    }
}
