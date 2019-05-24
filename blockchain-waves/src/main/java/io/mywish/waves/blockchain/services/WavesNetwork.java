package io.mywish.waves.blockchain.services;

import io.lastwill.eventscan.model.NetworkType;
import io.mywish.blockchain.WrapperBlock;
import io.mywish.blockchain.WrapperNetwork;
import io.mywish.blockchain.WrapperTransaction;
import io.mywish.blockchain.WrapperTransactionReceipt;
import io.mywish.waves.blockchain.model.WrapperTransactionWaves;
import io.mywish.wavescli4j.WavesClient;
import org.springframework.beans.factory.annotation.Autowired;

import java.math.BigInteger;
import java.util.List;

public class WavesNetwork extends WrapperNetwork {
    final private WavesClient wavesClient;

    @Autowired
    private WrapperBlockWavesService blockBuilder;

    @Autowired
    private WrapperTransactionReceiptWavesService transactionReceiptBuilder;

    public WavesNetwork(NetworkType type, WavesClient wabesClient) {
        super(type);
        this.wavesClient = wabesClient;
    }

    @Override
    public Long getLastBlock() throws Exception {
        return wavesClient.getHeight().getHeight();
    }

    @Override
    public WrapperBlock getBlock(String hash) throws Exception {
        throw new UnsupportedOperationException();
    }

    @Override
    public WrapperBlock getBlock(Long number) throws Exception {
        return blockBuilder.build(wavesClient.getBlock(number));
    }

    @Override
    public BigInteger getBalance(String address, Long blockNo) throws Exception {
        throw new UnsupportedOperationException();
    }

    @Override
    public WrapperTransactionReceipt getTxReceipt(WrapperTransaction transaction) {
        return transactionReceiptBuilder.build((WrapperTransactionWaves) transaction);
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
