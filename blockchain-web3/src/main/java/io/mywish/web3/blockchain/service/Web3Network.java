package io.mywish.web3.blockchain.service;

import io.lastwill.eventscan.model.NetworkType;
import io.mywish.blockchain.WrapperBlock;
import io.mywish.blockchain.WrapperNetwork;
import io.mywish.blockchain.WrapperTransaction;
import io.mywish.blockchain.WrapperTransactionReceipt;
import io.mywish.web3.blockchain.parity.Web3jEx;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.DefaultBlockParameterNumber;
import org.web3j.protocol.core.methods.response.Transaction;
import rx.Subscription;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.stream.Collectors;

@Slf4j
public class Web3Network extends WrapperNetwork {
    private final Web3j web3j;

    @Autowired
    private WrapperBlockWeb3Service blockBuilder;

    @Autowired
    private WrapperTransactionWeb3Service transactionBuilder;

    @Autowired
    private WrapperTransactionReceiptWeb3Service transactionReceiptBuilder;

    private final int pendingThreshold;

    private final BlockingQueue<Transaction> pendingTransactions = new LinkedBlockingQueue<>();
    private Subscription subscription;

    public Web3Network(NetworkType type, Web3j web3j, int pendingThreshold) {
        super(type);
        this.web3j = web3j;
        this.pendingThreshold = pendingThreshold;
    }

    @PostConstruct
    private void init() {
        if (pendingThreshold > 0 && !(web3j instanceof Web3jEx)) {
            log.info("Subscribe to pending transactions.");
            subscription = web3j.pendingTransactionObservable().subscribe(pendingTransactions::add);
        }
    }

    @PreDestroy
    private void close() {
        if (subscription == null) {
            return;
        }
        if (subscription.isUnsubscribed()) {
            return;
        }
        subscription.unsubscribe();
        subscription = null;
    }

    @Override
    public Long getLastBlock() throws Exception {
        return web3j.ethBlockNumber().send().getBlockNumber().longValue();
    }

    @Override
    public WrapperBlock getBlock(String hash) throws Exception {
        return blockBuilder.build(web3j.ethGetBlockByHash(hash, false).send().getBlock());
    }

    @Override
    public WrapperBlock getBlock(Long number) throws Exception {
        return blockBuilder.build(web3j.ethGetBlockByNumber(new DefaultBlockParameterNumber(number), true).send().getBlock());
    }

    @Override
    public BigInteger getBalance(String address, Long blockNo) throws Exception {
        return web3j
                .ethGetBalance(address, new DefaultBlockParameterNumber(blockNo))
                .send()
                .getBalance();
    }

    @Override
    public WrapperTransactionReceipt getTxReceipt(WrapperTransaction transaction) throws Exception {
        return transactionReceiptBuilder.build(
                web3j
                        .ethGetTransactionReceipt(transaction.getHash())
                        .send()
                        .getResult()
        );
    }

    @Override
    public boolean isPendingTransactionsSupported() {
        return true;
    }

    @Override
    public List<WrapperTransaction> fetchPendingTransactions() throws Exception {
        if (web3j instanceof Web3jEx) {
            return ((Web3jEx) web3j).parityGetPendingTransactions().send()
                    .getResult()
                    .stream()
                    .map(transactionBuilder::build)
                    .collect(Collectors.toList());
        }
        if (pendingTransactions.isEmpty()) {
            return Collections.emptyList();
        }
        ArrayList<WrapperTransaction> result = new ArrayList<>(pendingTransactions.size() + 3);
        while (!pendingTransactions.isEmpty()) {
            Transaction transaction = pendingTransactions.remove();
            result.add(transactionBuilder.build(transaction));
        }
        return result;
    }
}
