package io.mywish.bot.integration.services.impl;

import io.mywish.bot.integration.services.BlockchainExplorer;
import io.mywish.scanner.model.NetworkType;
import lombok.Getter;

public class EtherescanExplorer implements BlockchainExplorer {
    @Getter
    private final NetworkType networkType;
    private final String host;

    public EtherescanExplorer(boolean testnet) {
        this.host = testnet ? "ropsten.etherscan.io" : "etherscan.io";
        this.networkType = testnet ? NetworkType.ETHEREUM_ROPSTEN : NetworkType.ETHEREUM_MAINNET;
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
        return build("block", String.valueOf(blockNo));
    }

    @Override
    public String buildToTransaction(String txHash) {
        return build("tx", txHash);
    }

}
