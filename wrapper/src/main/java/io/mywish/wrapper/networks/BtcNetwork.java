package io.mywish.wrapper.networks;

import com.neemre.btcdcli4j.core.client.BtcdClient;
import io.mywish.wrapper.helpers.BtcBlockParser;
import io.lastwill.eventscan.model.NetworkType;
import io.mywish.wrapper.WrapperBlock;
import io.mywish.wrapper.WrapperNetwork;
import io.mywish.wrapper.block.WrapperBlockBtc;
import io.mywish.wrapper.WrapperTransaction;
import io.mywish.wrapper.WrapperTransactionReceipt;
import lombok.Getter;
import org.bitcoinj.core.NetworkParameters;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.IOException;
import java.math.BigInteger;

public class BtcNetwork extends WrapperNetwork {
    @Getter
    final private BtcdClient btcdClient;

    private final NetworkParameters networkParameters;

    @Autowired
    private BtcBlockParser btcBlockParser;

    public BtcNetwork(NetworkType type, BtcdClient btcdClient, NetworkParameters networkParameters) {
        super(type);
        this.btcdClient = btcdClient;
        this.networkParameters = networkParameters;
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
    public WrapperBlock getBlock(String hash) throws java.io.IOException {
        try {
            return new WrapperBlockBtc(btcBlockParser.parse(networkParameters, (String) btcdClient.getBlock(hash, false)), networkParameters);
        } catch (Exception e) {
            throw new java.io.IOException(e);
        }
    }

    @Override
    public WrapperBlock getBlock(Long number) throws java.io.IOException {
        try {
            String hash = btcdClient.getBlockHash(number.intValue());
            return getBlock(btcdClient.getBlockHash(number.intValue()));
        } catch (Exception e) {
            throw new java.io.IOException(e);
        }
    }

    @Override
    public WrapperTransaction getTransaction(String hash) throws java.io.IOException {
        throw new RuntimeException("Method not supported");
    }

    @Override
    public BigInteger getBalance(String address, Long blockNo) {
        throw new RuntimeException("Method not supported");
    }

    @Override
    public WrapperTransactionReceipt getTxReceipt(WrapperTransaction transaction) {
        throw new RuntimeException("Method not supported");
    }
}
