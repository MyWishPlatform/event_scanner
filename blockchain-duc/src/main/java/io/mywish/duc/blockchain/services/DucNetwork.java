package io.mywish.duc.blockchain.services;

import com.neemre.btcdcli4j.core.client.BtcdClient;
import io.mywish.duc.blockchain.helper.DucBlockParser;
import io.lastwill.eventscan.model.NetworkType;
import io.mywish.blockchain.WrapperBlock;
import io.mywish.blockchain.WrapperNetwork;
import io.mywish.blockchain.WrapperTransaction;
import io.mywish.blockchain.WrapperTransactionReceipt;
import org.bitcoinj.core.NetworkParameters;
import org.springframework.beans.factory.annotation.Autowired;
import java.math.BigInteger;
import java.util.List;

public class DucNetwork extends WrapperNetwork {
    final private BtcdClient ducdClient;

    @Autowired
    private WrapperBlockDucService blockBuilder;

    private final NetworkParameters networkParameters;

    @Autowired
    private DucBlockParser ducBlockParser;

    public DucNetwork(NetworkType type, BtcdClient ducdClient, NetworkParameters networkParameters) {
        super(type);
        this.ducdClient = ducdClient;
        this.networkParameters = networkParameters;
    }

    @Override
    public Long getLastBlock() throws Exception {
        return ducdClient.getBlockCount().longValue();
    }

    @Override
    public WrapperBlock getBlock(String hash) throws Exception {
        // TODO optimize
        long height = ducdClient.getBlock(hash).getHeight();
        return blockBuilder.build(
                ducBlockParser.parse(
                        networkParameters,
                        (String) ducdClient.getBlock(hash, false)
                ),
                height,
                networkParameters
        );
    }

    @Override
    public WrapperBlock getBlock(Long number) throws Exception {
        String hash = ducdClient.getBlockHash(number.intValue());
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
