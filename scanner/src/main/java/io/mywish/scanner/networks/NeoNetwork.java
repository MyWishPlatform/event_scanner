package io.mywish.scanner.networks;

import com.glowstick.neocli4j.NeoClient;
import io.mywish.scanner.Network;
import io.mywish.scanner.WrapperTransactionReceipt;
import io.mywish.scanner.model.NetworkType;
import lombok.Getter;
import java.math.BigInteger;

public class NeoNetwork extends Network {
    @Getter
    final private NeoClient neoClient;

    public NeoNetwork(NetworkType type, NeoClient neoClient) {
        super(type);
        this.neoClient = neoClient;
    }

    @Override
    public Long getLastBlock() throws java.io.IOException {
        return (long)neoClient.getBlockCount();
    }

    @Override
    public BigInteger getBalance(String address, Long blockNo) throws java.io.IOException {
        return neoClient.getBalance(address);
    }

    @Override
    public WrapperTransactionReceipt getTxReceipt(String hash) throws java.io.IOException {
        return null;
    }
}
