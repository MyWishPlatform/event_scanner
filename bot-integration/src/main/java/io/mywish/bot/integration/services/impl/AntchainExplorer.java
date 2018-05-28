package io.mywish.bot.integration.services.impl;

import io.lastwill.eventscan.model.NetworkType;
import io.mywish.bot.integration.services.BlockchainExplorer;
import lombok.Getter;

public class AntchainExplorer implements BlockchainExplorer {
    @Getter
    private final NetworkType networkType;
    private final String host;

    public AntchainExplorer(boolean testnet) {
        this.networkType = testnet ? NetworkType.NEO_TESTNET : NetworkType.NEO_MAINNET;
        this.host = (testnet ? "testnet." : "") + "antcha.in";
    }

    private String build(String argType, String arg) {
        return "https://" + host + "/" + argType + "/" + arg;
    }

    @Override
    public String buildToAddress(String address) {
        return build("address/info", address);
    }

    @Override
    public String buildToBlock(long blockNo) {
        return build("block/height", String.valueOf(blockNo));
    }

    @Override
    public String buildToTransaction(String txHash) {
        return build("tx/hash", txHash);
    }
}
