package io.lastwill.eventscan.model;

import lombok.Getter;

import java.util.EnumSet;

@Getter
public enum NetworkType {
    ETHEREUM_MAINNET(NetworkProviderType.WEB3),
    ETHEREUM_ROPSTEN(NetworkProviderType.WEB3),
    RSK_MAINNET(NetworkProviderType.WEB3),
    RSK_TESTNET(NetworkProviderType.WEB3),
    RSK_FEDERATION_MAINNET(NetworkProviderType.WEB3),
    RSK_FEDERATION_TESTNET(NetworkProviderType.WEB3),
    BTC_MAINNET(NetworkProviderType.BTC),
    BTC_TESTNET_3(NetworkProviderType.BTC),
    NEO_MAINNET(NetworkProviderType.NEO),
    NEO_TESTNET(NetworkProviderType.NEO),
    EOS_MAINNET(NetworkProviderType.EOS),
    EOS_TESTNET(NetworkProviderType.EOS),
    TRON_MAINNET(NetworkProviderType.TRON),
    TRON_TESTNET(NetworkProviderType.TRON),
    WAVES_MAINNET(NetworkProviderType.WAVES),
    WAVES_TESTNET(NetworkProviderType.WAVES),
    ;
    public final static String ETHEREUM_MAINNET_VALUE = "ETHEREUM_MAINNET";
    public final static String ETHEREUM_ROPSTEN_VALUE = "ETHEREUM_ROPSTEN";
    public final static String RSK_MAINNET_VALUE = "RSK_MAINNET";
    public final static String RSK_TESTNET_VALUE = "RSK_TESTNET";
    public final static String BTC_MAINNET_VALUE = "BTC_MAINNET";
    public final static String BTC_TESTNET_3_VALUE = "BTC_TESTNET_3";
    public final static String NEO_MAINNET_VALUE = "NEO_MAINNET";
    public final static String NEO_TESTNET_VALUE = "NEO_TESTNET";
    public final static String EOS_MAINNET_VALUE = "EOS_MAINNET";
    public final static String EOS_TESTNET_VALUE = "EOS_TESTNET";
    public final static String TRON_MAINNET_VALUE = "TRON_MAINNET";
    public final static String TRON_TESTNET_VALUE = "TRON_TESTNET";
    public final static String WAVES_MAINNET_VALUE = "WAVES_MAINNET";
    public final static String WAVES_TESTNET_VALUE = "WAVES_TESTNET";

    private final static EnumSet<NetworkType> namedEvents = EnumSet.of(NEO_MAINNET, NEO_TESTNET);

    private final NetworkProviderType networkProviderType;

    NetworkType(NetworkProviderType networkProviderType) {
        this.networkProviderType = networkProviderType;
    }

    public boolean isEventByName() {
        return namedEvents.contains(this);
    }
}
