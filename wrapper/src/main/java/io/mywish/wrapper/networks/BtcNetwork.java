package io.mywish.wrapper.networks;

import com.neemre.btcdcli4j.core.client.BtcdClient;
import io.mywish.wrapper.helpers.BtcBlockParser;
import io.lastwill.eventscan.model.NetworkType;
import io.mywish.wrapper.WrapperBlock;
import io.mywish.wrapper.WrapperNetwork;
import io.mywish.wrapper.WrapperTransaction;
import io.mywish.wrapper.WrapperTransactionReceipt;
import io.mywish.wrapper.service.block.WrapperBlockBtcService;
import org.bitcoinj.core.NetworkParameters;
import org.springframework.beans.factory.annotation.Autowired;
import java.math.BigInteger;

public class BtcNetwork extends WrapperNetwork {
    final private BtcdClient btcdClient;

    @Autowired
    private WrapperBlockBtcService blockBuilder;

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
        // TODO optimize
        long height = btcdClient.getBlock(hash).getHeight();
        return blockBuilder.build(
                btcBlockParser.parse(
                        networkParameters,
                        (String) btcdClient.getBlock(hash, false)
                ),
                height,
                networkParameters
        );
    }

    @Override
    public WrapperBlock getBlock(Long number) throws Exception {
        String hash = btcdClient.getBlockHash(number.intValue());
        return getBlock(hash);
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
