package io.mywish.waves.blockchain.services;

import com.wavesplatform.wavesj.Node;
import io.lastwill.eventscan.model.NetworkType;
import io.mywish.blockchain.WrapperBlock;
import io.mywish.blockchain.WrapperNetwork;
import io.mywish.blockchain.WrapperTransaction;
import io.mywish.blockchain.WrapperTransactionReceipt;
import io.mywish.waves.blockchain.model.WrapperTransactionWaves;
import org.springframework.beans.factory.annotation.Autowired;

import java.math.BigInteger;
import java.util.List;

public class WavesNetwork extends WrapperNetwork {
    final private Node wavesNode;

    @Autowired
    private WrapperBlockWavesService blockBuilder;

    @Autowired
    private WrapperTransactionReceiptWavesService transactionReceiptBuilder;

    public WavesNetwork(NetworkType type, Node wavesNode) {
        super(type);
        this.wavesNode = wavesNode;
    }

    @Override
    public Long getLastBlock() throws Exception {
        return (long) wavesNode.getHeight();
    }

    @Override
    public WrapperBlock getBlock(String hash) throws Exception {
        return blockBuilder.build(wavesNode.getBlock(hash));
    }

    @Override
    public WrapperBlock getBlock(Long number) throws Exception {
        return blockBuilder.build(wavesNode.getBlock(number.intValue()));
    }

    @Override
    public BigInteger getBalance(String address, Long blockNo) throws Exception {
        return BigInteger.valueOf(wavesNode.getBalance(address));
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
