package io.mywish.wrapper.networks;

import com.neemre.btcdcli4j.core.client.BtcdClient;
import io.mywish.wrapper.helpers.BtcBlockParser;
import io.lastwill.eventscan.model.NetworkType;
import io.mywish.wrapper.WrapperBlock;
import io.mywish.wrapper.WrapperNetwork;
import io.mywish.wrapper.block.WrapperBlockBtc;
import io.mywish.wrapper.WrapperTransaction;
import io.mywish.wrapper.WrapperTransactionReceipt;
import org.bitcoinj.core.NetworkParameters;
import org.springframework.beans.factory.annotation.Autowired;
import java.math.BigInteger;

public class BtcNetwork extends WrapperNetwork {
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
    public Long getLastBlock() throws Exception {
        return btcdClient.getBlockCount().longValue();
    }

    @Override
    public WrapperBlock getBlock(String hash) throws Exception {
        return new WrapperBlockBtc(btcBlockParser.parse(networkParameters, (String) btcdClient.getBlock(hash, false)), networkParameters);
    }

    @Override
    public WrapperBlock getBlock(Long number) throws Exception {
        String hash = btcdClient.getBlockHash(number.intValue());
        return getBlock(btcdClient.getBlockHash(number.intValue()));
    }

    @Override
    public WrapperTransaction getTransaction(String hash) throws Exception {
        throw new Exception("Method not supported");
    }

    @Override
    public BigInteger getBalance(String address, Long blockNo) throws Exception {
        throw new Exception("Method not supported");
    }

    @Override
    public WrapperTransactionReceipt getTxReceipt(WrapperTransaction transaction) throws Exception {
        throw new Exception("Method not supported");
    }
}
