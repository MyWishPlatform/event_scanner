package io.mywish.bot.integration.services;

import io.lastwill.eventscan.model.NetworkType;

public interface BlockchainExplorer {
    String buildToAddress(String address);
    String buildToBlock(long blockNo);
    String buildToTransaction(String txHash);
    NetworkType getNetworkType();
}
