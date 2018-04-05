package io.lastwill.eventscan.services;

import io.mywish.scanner.model.NetworkType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.web3j.protocol.Web3j;

import java.util.HashMap;
import java.util.Map;

@Component
public class Web3Provider {
    @Autowired
    private Map<String, Web3j> web3ByNetwork = new HashMap<>();

    public Web3j get(NetworkType networkType) {
        Web3j web3j = web3ByNetwork.get(networkType.name());
        if (web3j == null) {
            throw new UnsupportedOperationException("There is no web3 for " + networkType);
        }
        return web3j;
    }
}
