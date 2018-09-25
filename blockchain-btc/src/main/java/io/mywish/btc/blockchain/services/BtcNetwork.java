package io.mywish.btc.blockchain.services;

import com.neemre.btcdcli4j.core.client.BtcdClient;
import io.mywish.btc.blockchain.helper.BtcBlockParser;
import io.lastwill.eventscan.model.NetworkType;
import io.mywish.blockchain.WrapperBlock;
import io.mywish.blockchain.WrapperNetwork;
import io.mywish.blockchain.WrapperTransaction;
import io.mywish.blockchain.WrapperTransactionReceipt;
import org.bitcoinj.core.NetworkParameters;
import org.springframework.beans.factory.annotation.Autowired;
import java.math.BigInteger;
import java.util.List;

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
    public BigInteger getBalance(String address, Long blockNo) {
        throw new UnsupportedOperationException("Method not supported");
    }

    @Override
    public WrapperTransactionReceipt getTxReceipt(WrapperTransaction transaction) {
        throw new UnsupportedOperationException("Method not supported");
    }

    @Override
    public boolean isPendingTransactionsSupported() {
        return false;
    }

    @Override
    public List<WrapperTransaction> fetchPendingTransactions() {
        throw new UnsupportedOperationException("Method not supported");
//        return Collections.emptyList();
    }
}
