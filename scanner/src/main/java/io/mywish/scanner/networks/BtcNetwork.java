package io.mywish.scanner.networks;

import com.neemre.btcdcli4j.core.client.BtcdClient;
import io.mywish.scanner.Network;
import io.mywish.scanner.WrapperTransactionReceipt;
import io.mywish.scanner.model.NetworkType;
import lombok.Getter;

import java.io.IOException;
import java.math.BigInteger;

public class BtcNetwork extends Network {
    @Getter
    final private BtcdClient btcdClient;

    public BtcNetwork(NetworkType type, BtcdClient btcdClient) {
        super(type);
        this.btcdClient = btcdClient;
    }

    @Override
    public Long getLastBlock() throws IOException {
        try {
            return btcdClient.getBlockCount().longValue();
        } catch (Exception e) {
            throw new IOException(e);
        }
    }

    @Override
    public BigInteger getBalance(String address, Long blockNo) {
        throw new RuntimeException("Method not supported");
    }

    @Override
    public WrapperTransactionReceipt getTxReceipt(String hash) {
        throw new RuntimeException("Method not supported");
    }
}
