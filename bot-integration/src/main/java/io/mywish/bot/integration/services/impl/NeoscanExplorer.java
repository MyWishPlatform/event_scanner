package io.mywish.bot.integration.services.impl;

import io.lastwill.eventscan.model.NetworkType;
import io.mywish.bot.integration.services.BlockchainExplorer;
import lombok.Getter;

public class NeoscanExplorer implements BlockchainExplorer {
    @Getter
    private final NetworkType networkType;
    private final String host;

    public NeoscanExplorer(boolean testnet) {
        this.networkType = testnet ? NetworkType.NEO_TESTNET : NetworkType.NEO_MAINNET;
        this.host = "neoscan.mywish.io";
    }

    private String build(String argType, String arg) {
        return "http://" + host + "/" + argType + "/" + arg;
    }

    @Override
    public String buildToAddress(String address) {
        return build("address", address);
    }

    @Override
    public String buildToBlock(long blockNo) {
        return build("block", String.valueOf(blockNo));
    }

    @Override
    public String buildToTransaction(String txHash) {
        return build("transaction", txHash);
    }
}
