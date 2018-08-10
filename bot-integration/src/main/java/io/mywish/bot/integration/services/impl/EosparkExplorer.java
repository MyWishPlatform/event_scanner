package io.mywish.bot.integration.services.impl;

import io.lastwill.eventscan.model.NetworkType;
import io.mywish.bot.integration.services.BlockchainExplorer;
import org.springframework.stereotype.Component;

@Component
public class EosparkExplorer implements BlockchainExplorer {
    @Override
    public String buildToAddress(String address) {
        return "https://eospark.com/Jungle/account/" + address;
    }

    @Override
    public String buildToBlock(long blockNo) {
        return "https://eospark.com/Jungle/block/" + blockNo;
    }

    @Override
    public String buildToTransaction(String txHash) {
        return "https://eospark.com/Jungle/tx/" + txHash;
    }

    @Override
    public NetworkType getNetworkType() {
        return NetworkType.EOS_TESTNET;
    }
}
