package io.mywish.scanner.services;

import io.mywish.scanner.model.NetworkType;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.util.concurrent.atomic.AtomicBoolean;

@Slf4j
public abstract class Scanner {
    public static final long INFO_INTERVAL = 60000;
    public static final long WARN_INTERVAL = 120000;

    @Getter
    protected final NetworkType networkType;
    protected final LastBlockPersister lastBlockPersister;

    @Getter
    private long pollingInterval = 10000;
    @Getter
    private int commitmentChainLength = 5;

    private final AtomicBoolean isTerminated = new AtomicBoolean(false);

    protected Long lastBlockNo;
    protected Long nextBlockNo;
    protected long lastBlockIncrementTimestamp;

    @Autowired
    protected EventPublisher eventPublisher;

    private final Object sync = new Object();

    private final Runnable poller = new Runnable() {
        @Override
        public void run() {
            while (!isTerminated.get()) {
                try {
                    long start = System.currentTimeMillis();
                    lastBlockNo = getLastBlock();
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

    abstract protected Long getLastBlock() throws Exception;
    abstract protected void loadNextBlock() throws Exception;

    public Scanner(NetworkType networkType, LastBlockPersister lastBlockPersister) {
        this.networkType = networkType;
        this.lastBlockPersister = lastBlockPersister;
    }

    public void setPollingInterval(long pollingInterval) {
        if (pollingInterval > 0) this.pollingInterval = pollingInterval;
    }

    public void setCommitmentChainLength(int commitmentChainLength) {
        if (commitmentChainLength > 0) this.commitmentChainLength = commitmentChainLength;
    }

    @PostConstruct
    public void open() throws Exception {
        lastBlockPersister.open();
        nextBlockNo = lastBlockPersister.getLastBlock();
        try {
            lastBlockNo = getLastBlock();
            lastBlockIncrementTimestamp = System.currentTimeMillis();
            if (nextBlockNo == null) {
                nextBlockNo = lastBlockNo - commitmentChainLength;
            }
            log.info("{} RPC: latest block is {} but next is {}.", networkType, lastBlockNo, nextBlockNo);
        }
        catch (Exception e) {
            log.error("{} sending failed.", networkType);
            throw e;
        }

        pollerThread.start();
        log.info("Subscribed to {} new block event.", networkType);
    }

    @PreDestroy
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
}
