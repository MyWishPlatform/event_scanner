package io.mywish.scanner.services;

import io.mywish.scanner.model.NewPendingTransactionsEvent;
import io.mywish.blockchain.WrapperBlock;
import io.mywish.blockchain.WrapperNetwork;
import io.mywish.blockchain.WrapperTransaction;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

@Slf4j
public abstract class ScannerPolling extends Scanner {
    private final Object sync = new Object();

    private Long lastBlockNo;

    @Getter
    private int commitmentChainLength;
    @Getter
    private long pollingInterval;
    @Getter
    private final long reachInterval;

    protected final AtomicBoolean isTerminated = new AtomicBoolean(false);

    private final Runnable poller = () -> {
        while (!isTerminated.get()) {
            try {
                long start = System.currentTimeMillis();
                lastBlockNo = network.getLastBlock();
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
                    log.warn("{}: there is no block from {} ms!", network.getType(), interval);
                }
                else if (interval > INFO_INTERVAL) {
                    log.info("{}: there is no block from {} ms.", network.getType(), interval);
                }

                if (network.isPendingTransactionsSupported()) {
                    log.debug("Get actual list of pending.");
                    List<WrapperTransaction> pendingTxs = network.fetchPendingTransactions();
                    if (!pendingTxs.isEmpty()) {
                        eventPublisher.publish(new NewPendingTransactionsEvent(
                                network.getType(),
                                pendingTxs
                        ));
                    }
                }

                log.debug("All blocks processed, wait new one.");
                synchronized (sync) {
                    sync.wait(pollingInterval);
                }
            }
            catch (InterruptedException e) {
                log.warn("{}: polling cycle was interrupted.", network.getType(), e);
                break;
            }
            catch (Throwable e) {
                log.error("{}: exception handled in polling cycle. Continue.", network.getType(), e);
                try {
                    Thread.sleep(pollingInterval);
                }
                catch (InterruptedException e1) {
                    log.warn("{}: polling cycle was interrupted after error.", network.getType(), e1);
                    break;
                }
            }
        }
    };

    private void loadNextBlock() throws Exception {
        long delta = lastBlockNo - nextBlockNo;
        if (delta <= getCommitmentChainLength()) {
            return;
        }

        long start = System.currentTimeMillis();
        if (reachInterval > 0) {
            Thread.sleep(reachInterval);
        }
        WrapperBlock block = network.getBlock(nextBlockNo);
        if (log.isDebugEnabled()) {
            log.debug("Get next block: {} ms.", System.currentTimeMillis() - start);
        }

        lastBlockIncrementTimestamp = System.currentTimeMillis();

        lastBlockPersister.saveLastBlock(nextBlockNo);
        nextBlockNo++;

        processBlock(block);
    }


    public ScannerPolling(WrapperNetwork network, LastBlockPersister lastBlockPersister, Long pollingInterval, Integer commitmentChainLength) {
        super(network, lastBlockPersister);
        this.setWorker(poller);
        this.commitmentChainLength = commitmentChainLength;
        this.pollingInterval = pollingInterval;
        this.reachInterval = 0L;
    }

    public ScannerPolling(WrapperNetwork network, LastBlockPersister lastBlockPersister, Long pollingInterval, Integer commitmentChainLength, Long reachInterval) {
        super(network, lastBlockPersister);
        this.setWorker(poller);
        this.commitmentChainLength = commitmentChainLength;
        this.pollingInterval = pollingInterval;
        this.reachInterval = reachInterval;
    }

    @PostConstruct
    @Override
    protected void open() throws Exception {
        lastBlockPersister.open();
        nextBlockNo = lastBlockPersister.getLastBlock();
        try {
            lastBlockNo = network.getLastBlock();
            lastBlockIncrementTimestamp = System.currentTimeMillis();
            if (nextBlockNo == null) {
                nextBlockNo = lastBlockNo - commitmentChainLength;
            }
            log.info("{} RPC: latest block is {} but next is {}.", network.getType(), lastBlockNo, nextBlockNo);
        }
        catch (Exception e) {
            log.error("{} sending failed.", network.getType());
            throw e;
        }
    }

    @PreDestroy
    @Override
    protected void close() {
        try {
            lastBlockPersister.close();
        }
        catch (Exception e) {
            log.warn("Persister for {} closing failed.", network.getType(), e);
        }
        isTerminated.set(true);
        log.info("Wait {} ms till cycle is completed for {}.", pollingInterval + 1, network.getType());
        synchronized (sync) {
            sync.notifyAll();
        }
    }
}
