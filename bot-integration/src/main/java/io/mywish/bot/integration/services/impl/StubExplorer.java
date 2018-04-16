package io.mywish.bot.integration.services.impl;

import io.mywish.bot.integration.services.BlockchainExplorer;
import io.mywish.scanner.model.NetworkType;
import org.springframework.stereotype.Component;

@Component
public class StubExplorer implements BlockchainExplorer {
    @Override
    public String buildToAddress(String address) {
        return address;
    }

    @Override
    public String buildToBlock(long blockNo) {
        return String.valueOf(blockNo);
    }

    @Override
    public String buildToTransaction(String txHash) {
        return txHash;
    }

    @Override
    public NetworkType getNetworkType() {
        return null;
    }
}
