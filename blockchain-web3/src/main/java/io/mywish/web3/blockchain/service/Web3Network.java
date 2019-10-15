package io.mywish.web3.blockchain.service;

import io.lastwill.eventscan.model.NetworkType;
import io.mywish.blockchain.WrapperBlock;
import io.mywish.blockchain.WrapperNetwork;
import io.mywish.blockchain.WrapperTransaction;
import io.mywish.blockchain.WrapperTransactionReceipt;
import io.reactivex.disposables.Disposable;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.Web3jService;
import org.web3j.protocol.core.DefaultBlockParameterNumber;
import org.web3j.protocol.core.methods.response.Transaction;
import org.web3j.protocol.websocket.WebSocketService;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.math.BigInteger;
import java.net.ConnectException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

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
    private Disposable subscription;

    public Web3Network(NetworkType type, Web3jService web3jService, int pendingThreshold) throws ConnectException {
        super(type);
        if (web3jService instanceof WebSocketService) {
            ((WebSocketService) web3jService).connect();
        }
        this.web3j = Web3j.build(web3jService);
        this.pendingThreshold = pendingThreshold;
    }

    @PostConstruct
    private void init() {
        if (pendingThreshold > 0) {
            log.info("Subscribe to pending transactions.");
            subscription = web3j.pendingTransactionFlowable().subscribe(pendingTransactions::add);
        }
    }

    @PreDestroy
    private void close() {
        if (subscription == null) {
            return;
        }
        if (subscription.isDisposed()) {
            return;
        }
        subscription.dispose();
        subscription = null;
        web3j.shutdown();
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
