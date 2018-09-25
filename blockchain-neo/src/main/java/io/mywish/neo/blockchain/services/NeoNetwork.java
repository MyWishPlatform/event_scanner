package io.mywish.neo.blockchain.services;

import io.mywish.neocli4j.NeoClient;
import io.mywish.blockchain.WrapperNetwork;
import io.mywish.blockchain.WrapperTransactionReceipt;
import io.lastwill.eventscan.model.NetworkType;
import io.mywish.blockchain.*;
import io.mywish.neo.blockchain.model.WrapperTransactionNeo;
import org.springframework.beans.factory.annotation.Autowired;

import java.math.BigInteger;
import java.util.List;

public class NeoNetwork extends WrapperNetwork {
    final private NeoClient neoClient;

    @Autowired
    private WrapperBlockNeoService blockBuilder;

    @Autowired
    private WrapperTransactionNeoService transactionBuilder;

    @Autowired
    private WrapperTransactionReceiptNeoService transactionReceiptBuilder;

    public NeoNetwork(NetworkType type, NeoClient neoClient) {
        super(type);
        this.neoClient = neoClient;
    }

    @Override
    public Long getLastBlock() throws Exception {
        return (long)neoClient.getBlockCount();
    }

    @Override
    public WrapperBlock getBlock(String hash) throws Exception {
        return blockBuilder.build(neoClient.getBlock(hash));
    }

    @Override
    public WrapperBlock getBlock(Long number) throws Exception {
        return blockBuilder.build(neoClient.getBlock(number));
    }

    @Override
    public BigInteger getBalance(String address, Long blockNo) throws Exception {
        return neoClient.getBalance(address);
    }

    @Override
    public WrapperTransactionReceipt getTxReceipt(WrapperTransaction transaction) throws Exception {
        return transactionReceiptBuilder.build(
                (WrapperTransactionNeo) transaction,
                neoClient.getEvents(transaction.getHash())
        );
    }

    @Override
    public boolean isPendingTransactionsSupported() {
        return false;
    }

    @Override
    public List<WrapperTransaction> fetchPendingTransactions() {
        throw new UnsupportedOperationException("fetchPendingTransactions is not supported.");
    }
}
