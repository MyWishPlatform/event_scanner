package io.mywish.scanner.services;

import io.mywish.scanner.model.NetworkType;
import io.mywish.scanner.model.NewBlockEvent;
import io.mywish.scanner.model.NewTransactionEvent;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.CollectionUtils;
import org.springframework.util.MultiValueMap;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.DefaultBlockParameterNumber;
import org.web3j.protocol.core.Response;
import org.web3j.protocol.core.methods.response.EthBlock;
import org.web3j.protocol.core.methods.response.EthBlock.Block;
import org.web3j.protocol.core.methods.response.Transaction;
import org.web3j.protocol.core.methods.response.TransactionReceipt;

import java.io.IOException;
import java.util.HashMap;
import java.util.concurrent.atomic.AtomicBoolean;

@Slf4j
public class Web3Scanner {
    public static final long INFO_INTERVAL = 60000;
    public static final long WARN_INTERVAL = 120000;

    @Getter
    private final NetworkType networkType;
    private final Web3j web3j;
    private final EventPublisher eventPublisher;
    private final LastBlockPersister lastBlockPersister;

    private final long pollingInterval;
    private final int commitmentChainLength;

    private final AtomicBoolean isTerminated = new AtomicBoolean(false);

    private Long lastBlockNo;
    private Long nextBlockNo;
    private long lastBlockIncrementTimestamp;

    private final Object sync = new Object();

    private final Runnable poller = new Runnable() {
        @Override
        public void run() {
            while (!isTerminated.get()) {
                try {
                    long start = System.currentTimeMillis();
                    lastBlockNo = web3j.ethBlockNumber().send().getBlockNumber().longValue();
                    if (log.isDebugEnabled()) {
                        log.debug("Get actual block no: {} ms.", System.currentTimeMillis() - start);
                    }

                    loadNextBlock();

                    if (lastBlockNo - nextBlockNo > commitmentChainLength) {
                        log.debug("Process next block {}/{} immediately.", nextBlockNo, lastBlockNo);
                        continue;
                    }

                    long interval = System.currentTimeMillis() - lastBlockIncrementTimestamp;
                    if (interval > WARN_INTERVAL) {
                        log.warn("{}: there is no block from {} ms!", networkType, interval);
                    }
                    else if (interval > INFO_INTERVAL) {
                        log.info("{}: there is no block from {} ms.", networkType, interval);
                    }

                    log.debug("All blocks processed, wait new one.");
                    synchronized (sync) {
                        sync.wait(pollingInterval);
                    }
                }
                catch (InterruptedException e) {
                    log.warn("{}: polling cycle was interrupted.", networkType, e);
                    break;
                }
                catch (Throwable e) {
                    log.error("{}: exception handled in polling cycle. Continue.", networkType, e);
                    try {
                        Thread.sleep(pollingInterval);
                    }
                    catch (InterruptedException e1) {
                        log.warn("{}: polling cycle was interrupted after error.", networkType, e1);
                        break;
                    }
                }
            }
        }
    };

    private final Thread pollerThread = new Thread(poller);

    public Web3Scanner(NetworkType networkType, Web3j web3j, EventPublisher eventPublisher, LastBlockPersister lastBlockPersister, long pollingInterval, int commitmentChainLength) {
        this.networkType = networkType;
        this.web3j = web3j;
        this.eventPublisher = eventPublisher;
        this.lastBlockPersister = lastBlockPersister;
        this.pollingInterval = pollingInterval;
        this.commitmentChainLength = commitmentChainLength;
    }

    public void open() throws IOException {
        lastBlockPersister.open();
        nextBlockNo = lastBlockPersister.getLastBlock();
        try {
            boolean syncing = web3j.ethSyncing().send().isSyncing();
            lastBlockNo = web3j.ethBlockNumber().send().getBlockNumber().longValue();
            lastBlockIncrementTimestamp = System.currentTimeMillis();
            if (nextBlockNo == null) {
                nextBlockNo = lastBlockNo - commitmentChainLength;
            }
            log.info("Web3 {} syncing status: {}, latest block is {} but next is {}.", networkType, syncing ? "syncing" : "synced", lastBlockNo, nextBlockNo);
//            blockFilter = web3j.ethNewBlockFilter().send();
//            log.info("Block filter was created with id {}.", blockFilter.getFilterId());
        }
        catch (IOException e) {
            log.error("Web3 {} sending failed.", networkType);
            throw e;
        }

        pollerThread.start();
        log.info("Subscribed to web3 {} new block event.", networkType);
    }

    public void close() {
        try {
            lastBlockPersister.close();
        }
        catch (Exception e) {
            log.warn("Persister for {} closing failed.", networkType, e);
        }
        isTerminated.set(true);
        log.info("Wait {} ms till cycle is completed for {}.", pollingInterval + 1, networkType);
        synchronized (sync) {
            sync.notifyAll();
        }
    }

    private void loadNextBlock() throws Exception {
        long delta = lastBlockNo - nextBlockNo;
        if (delta <= commitmentChainLength) {
            return;
        }

        long start = System.currentTimeMillis();
        EthBlock result = web3j.ethGetBlockByNumber(new DefaultBlockParameterNumber(nextBlockNo), true)
                .send();
        if (log.isDebugEnabled()) {
            log.debug("Get next block: {} ms.", System.currentTimeMillis() - start);
        }

        if (result.getBlock() == null) {
            throw new Exception("Result has null block when it was received by number " + nextBlockNo);
        }

        if (result.hasError()) {
            Response.Error error = result.getError();
            throw new Exception("Result with error: code " + error.getCode() + ", message '" + error.getMessage() + "' and data " + error.getData());
        }

        if (result.getBlock() == null) {
            throw new Exception("Result with null block!");
        }

        lastBlockIncrementTimestamp = System.currentTimeMillis();

        lastBlockPersister.saveLastBlock(nextBlockNo);
        nextBlockNo ++;

        processBlock(result);
    }

    private void processBlock(EthBlock ethBlock) {
        Block block = ethBlock.getBlock();
        log.debug("New bock received {} ({})", block.getNumber(), block.getHash());

        MultiValueMap<String, Transaction> addressTransactions = CollectionUtils.toMultiValueMap(new HashMap<>());

        block.getTransactions()
                .stream()
                .map(result -> (EthBlock.TransactionObject) result)
                .map(EthBlock.TransactionObject::get)
                .forEach(transaction -> {
                    if (transaction.getFrom() != null) {
                        addressTransactions.add(transaction.getFrom().toLowerCase(), transaction);
                    }
                    else {
                        log.warn("Empty from field for transaction {}. Skip it.", transaction.getHash());
                    }
                    if (transaction.getTo() != null) {
                        addressTransactions.add(transaction.getTo().toLowerCase(), transaction);
                    }
                    else {
                        if (transaction.getCreates() != null) {
                            addressTransactions.add(transaction.getCreates().toLowerCase(), transaction);
                        }
                        else {
                            try {
                                TransactionReceipt receipt = web3j.ethGetTransactionReceipt(transaction.getHash())
                                        .send()
                                        .getTransactionReceipt()
                                        .orElseThrow(() -> new Exception("Empty transaction receipt in result."));
                                transaction.setCreates(receipt.getContractAddress());
                                addressTransactions.add(receipt.getContractAddress().toLowerCase(), transaction);
                            }
                            catch (Exception e) {
                                log.error("Error on getting transaction {} receipt.", transaction.getHash(), e);
                                log.warn("Empty to and creates field for transaction {}. Skip it.", transaction.getHash());
                            }
                        }

                    }
                    eventPublisher.publish(new NewTransactionEvent(networkType, block, transaction));
                });

        eventPublisher.publish(new NewBlockEvent(networkType, block, addressTransactions));
    }
}
