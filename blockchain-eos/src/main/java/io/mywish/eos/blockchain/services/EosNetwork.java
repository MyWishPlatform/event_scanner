package io.mywish.eos.blockchain.services;

import io.lastwill.eventscan.model.NetworkType;
import io.mywish.blockchain.WrapperBlock;
import io.mywish.blockchain.WrapperNetwork;
import io.mywish.blockchain.WrapperTransaction;
import io.mywish.blockchain.WrapperTransactionReceipt;
import io.mywish.eoscli4j.EosClient;
import io.mywish.eoscli4j.model.BlockReliability;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;

import javax.annotation.PreDestroy;
import java.math.BigInteger;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

@Slf4j
public class EosNetwork extends WrapperNetwork {
    private final EosClient eosClient;
    private AtomicBoolean continueSubscription = new AtomicBoolean(true);

    @Autowired
    private WrapperBlockEosService blockBuilder;

    @Autowired
    private WrapperTransactionReceiptEosService transactionReceiptBuilder;

    public EosNetwork(NetworkType type, EosClient eosClient) {
        super(type);
        this.eosClient = eosClient;
    }

    @Override
    public Long getLastBlock() throws Exception {
        return eosClient.getChainInfo().getLastIrreversibleBlockNum();
    }

    @Override
    public BigInteger getBalance(String address, Long blockNo) throws Exception {
        return eosClient.getBalance("EOS", address).getValue();
    }

    @Override
    public WrapperBlock getBlock(String hash) throws Exception {
        return blockBuilder.build(eosClient.getBlock(hash));
    }

    @Override
    public WrapperBlock getBlock(Long number) throws Exception {
        return blockBuilder.build(eosClient.getBlock(number));
    }

    @Override
    public WrapperTransactionReceipt getTxReceipt(WrapperTransaction transaction) throws Exception {
        return transactionReceiptBuilder.build(transaction);
    }

    @Override
    public boolean isPendingTransactionsSupported() {
        return false;
    }

    @Override
    public List<WrapperTransaction> fetchPendingTransactions() throws Exception {
        throw new Exception("Method not supported");
    }

    public interface EosBlockCallback {
        void callback(WrapperBlock block);
    }

    public void subscribe(Long lastBlock, EosBlockCallback callback, BlockReliability blockReliability) throws Exception {
        final AtomicInteger counter = new AtomicInteger(0);
        eosClient.subscribe(lastBlock, eosBlock -> {
            if (counter.incrementAndGet() == 10) {
                log.info("{}: 10 blocks received, the last {} ({})", getType(), eosBlock.getBlockNum(), eosBlock.getId());
                counter.set(0);
            }
            callback.callback(blockBuilder.build(eosBlock));
            return continueSubscription.get();
        }, blockReliability);
    }

    @PreDestroy
    public void close() {
        log.info("Terminate {} network.", getType());
        continueSubscription.set(false);
    }
}
