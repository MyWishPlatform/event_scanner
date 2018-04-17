package io.mywish.bot.integration.services;

import io.mywish.scanner.model.NetworkType;

import java.net.URL;

public interface BlockchainExplorer {
    String buildToAddress(String address);
    String buildToBlock(long blockNo);
    String buildToTransaction(String txHash);
    NetworkType getNetworkType();
}
