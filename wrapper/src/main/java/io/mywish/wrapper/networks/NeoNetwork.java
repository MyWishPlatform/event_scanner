package io.mywish.wrapper.networks;

import io.mywish.neocli4j.NeoClient;
import io.mywish.wrapper.WrapperNetwork;
import io.mywish.wrapper.WrapperTransactionReceipt;
import io.lastwill.eventscan.model.NetworkType;
import io.mywish.wrapper.*;
import io.mywish.wrapper.service.block.WrapperBlockNeoService;
import io.mywish.wrapper.service.transaction.WrapperTransactionNeoService;
import io.mywish.wrapper.service.transaction.receipt.WrapperTransactionReceiptNeoService;
import io.mywish.wrapper.transaction.WrapperTransactionNeo;
import org.springframework.beans.factory.annotation.Autowired;

import java.math.BigInteger;
import java.util.ArrayList;
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
