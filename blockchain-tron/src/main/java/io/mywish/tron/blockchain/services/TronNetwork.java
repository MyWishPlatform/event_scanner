package io.mywish.tron.blockchain.services;

import io.lastwill.eventscan.model.NetworkType;
import io.mywish.blockchain.WrapperBlock;
import io.mywish.blockchain.WrapperNetwork;
import io.mywish.blockchain.WrapperTransaction;
import io.mywish.blockchain.WrapperTransactionReceipt;
import io.mywish.tron.blockchain.model.WrapperTransactionTron;
import io.mywish.troncli4j.TronClient;
import org.springframework.beans.factory.annotation.Autowired;

import java.math.BigInteger;
import java.util.List;

public class TronNetwork extends WrapperNetwork {
    final private TronClient tronClient;

    @Autowired
    private WrapperBlockTronService blockBuilder;

    @Autowired
    private WrapperTransactionReceiptTronService transactionReceiptBuilder;

    public TronNetwork(NetworkType type, TronClient neoClient) {
        super(type);
        this.tronClient = neoClient;
    }

    @Override
    public Long getLastBlock() throws Exception {
        return tronClient.getNodeInfo().getBlock().getNum();
    }

    @Override
    public WrapperBlock getBlock(String hash) throws Exception {
        return blockBuilder.build(tronClient.getBlock(hash));
    }

    @Override
    public WrapperBlock getBlock(Long number) throws Exception {
        return blockBuilder.build(tronClient.getBlock(number));
    }

    @Override
    public BigInteger getBalance(String address, Long blockNo) throws Exception {
        return tronClient.getAccount(address).getBalance();
    }

    @Override
    public WrapperTransactionReceipt getTxReceipt(WrapperTransaction transaction) throws Exception {
        return transactionReceiptBuilder.build(
                (WrapperTransactionTron) transaction,
                tronClient.getEventResult(transaction.getHash())
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
