package io.mywish.bot.integration.services.impl;

import io.lastwill.eventscan.model.NetworkType;
import io.mywish.bot.integration.services.BlockchainExplorer;
import org.springframework.stereotype.Component;

@Component
public class EosflareExplorer implements BlockchainExplorer {
    @Override
    public String buildToAddress(String address) {
        return "https://eosflare.io/account/" + address;
    }

    @Override
    public String buildToBlock(long blockNo) {
        return "https://eospark.com/block/" + blockNo;
    }

    @Override
    public String buildToTransaction(String txHash) {
        return "https://eosflare.io/tx/" + txHash;
    }

    @Override
    public NetworkType getNetworkType() {
        return NetworkType.EOS_MAINNET;
    }
}
