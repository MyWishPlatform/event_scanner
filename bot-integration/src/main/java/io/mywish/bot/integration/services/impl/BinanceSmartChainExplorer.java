package io.mywish.bot.integration.services.impl;

import io.lastwill.eventscan.model.NetworkType;
import io.mywish.bot.integration.services.BlockchainExplorer;
import lombok.Getter;

public class BinanceSmartChainExplorer implements BlockchainExplorer {
    @Getter
    private final NetworkType networkType;
    private final String host;

    public BinanceSmartChainExplorer(boolean testnet) {
        this.host = testnet ? "explorer.binance.org/smart-testnet" : "explorer.binance.org/smart-testnet";
        this.networkType = testnet ? NetworkType.BINANCE_SMART_TESTNET : NetworkType.BINANCE_SMART_MAINNET;
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
