package io.lastwill.eventscan.services;

import io.lastwill.eventscan.model.NetworkType;
import org.springframework.stereotype.Component;

@Component
public class Btc2RskNetworkConverter {
    public NetworkType convert(NetworkType sourceNetwork) {
        switch (sourceNetwork) {
            case BTC_MAINNET:
                return NetworkType.RSK_MAINNET;
            case BTC_TESTNET_3:
                return NetworkType.RSK_TESTNET;
            default:
                throw new UnsupportedOperationException("Support only BTC networks, " + sourceNetwork + " doesn't supported.");
        }
    }
}
