package io.lastwill.eventscan.model;

import java.util.EnumSet;

public enum NetworkType {
    ETHEREUM_MAINNET,
    ETHEREUM_ROPSTEN,
    RSK_MAINNET,
    RSK_TESTNET,
    RSK_FEDERATION_MAINNET,
    RSK_FEDERATION_TESTNET,
    BTC_MAINNET,
    BTC_TESTNET_3,
    NEO_MAINNET,
    NEO_TESTNET,
    EOS_MAINNET,
    EOS_TESTNET
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

    private final static EnumSet<NetworkType> namedEvents = EnumSet.of(NEO_MAINNET, NEO_TESTNET);

    public boolean isEventByName() {
        return namedEvents.contains(this);
    }
}
