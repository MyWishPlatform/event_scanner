package io.mywish.bot.integration.services.impl;

import io.lastwill.eventscan.model.NetworkType;
import io.mywish.bot.integration.services.BlockchainExplorer;
import lombok.Getter;

public class WavesExplorer implements BlockchainExplorer {
    @Getter
    private final NetworkType networkType;
    private final String host;

    public WavesExplorer(boolean isTestnet) {
        this.host =  "wavesexplorer.com" + (isTestnet ? "/testnet" : "");
        this.networkType = isTestnet ? NetworkType.WAVES_TESTNET : NetworkType.WAVES_MAINNET;
    }

    private String build(String argType, String arg) {
        return "https://" + host + "/" + argType + "/" + arg;
    }

    @Override
    public String buildToAddress(String address) {
        return build("address", address);
    }

    @Override
    public String buildToBlock(long blockNo) {
        return build("blocks", String.valueOf(blockNo));
    }

    @Override
    public String buildToTransaction(String txHash) {
        return build("tx", txHash);
    }
}
