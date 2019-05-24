package io.mywish.binance.blockchain.services;

import com.binance.dex.api.client.BinanceDexApiNodeClient;
import com.binance.dex.api.client.domain.BlockMeta;
import com.binance.dex.api.client.domain.broadcast.Transaction;
import io.lastwill.eventscan.model.NetworkType;
import io.mywish.binance.blockchain.model.BinanceBlock;
import io.mywish.blockchain.WrapperBlock;
import io.mywish.blockchain.WrapperNetwork;
import io.mywish.blockchain.WrapperTransaction;
import io.mywish.blockchain.WrapperTransactionReceipt;
import org.springframework.beans.factory.annotation.Autowired;

import java.math.BigInteger;
import java.util.List;

public class BinanceNetwork extends WrapperNetwork {
    final private BinanceDexApiNodeClient binanceClient;

    @Autowired
    private WrapperBlockBinanceService blockBuilder;

    public BinanceNetwork(NetworkType type, BinanceDexApiNodeClient binanceClient) {
        super(type);
        this.binanceClient = binanceClient;
    }

    @Override
    public Long getLastBlock() {
        return binanceClient.getNodeInfo().getSyncInfo().getLatestBlockHeight();
    }

    @Override
    public WrapperBlock getBlock(String hash) {
        BlockMeta meta = binanceClient.getBlockMetaByHash(hash);
        List<Transaction> transactions = binanceClient.getBlockTransactions(meta.getHeader().getHeight());
        return blockBuilder.build(new BinanceBlock(meta, transactions));
    }

    @Override
    public WrapperBlock getBlock(Long number) {
        return blockBuilder.build(new BinanceBlock(
                binanceClient.getBlockMetaByHeight(number),
                binanceClient.getBlockTransactions(number)
        ));
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
        throw new UnsupportedOperationException("fetchPendingTransactions is not supported.");
    }
}
