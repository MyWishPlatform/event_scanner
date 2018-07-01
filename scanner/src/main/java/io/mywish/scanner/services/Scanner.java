package io.mywish.scanner.services;

import io.mywish.wrapper.WrapperBlock;
import io.mywish.wrapper.WrapperNetwork;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.EventListener;
import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.concurrent.atomic.AtomicBoolean;

@Slf4j
public abstract class Scanner {
    public static final long INFO_INTERVAL = 60000;
    public static final long WARN_INTERVAL = 120000;

    protected final WrapperNetwork network;
    protected final LastBlockPersister lastBlockPersister;

    @Value("${etherscanner.pending-transactions-threshold:0}")
    private int transactionsThreshold;
    private PendingTransactionService pendingTransactionService;

    @Getter
    private long pollingInterval;
    @Getter
    private int commitmentChainLength;

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

                    if (pendingTransactionService != null) {
                        log.debug("Get actual list of pending.");

                        pendingTransactionService.updatePending(
                                network.fetchPendingTransactions(),
                                LocalDateTime.ofEpochSecond(start, 0, ZoneOffset.UTC)
                        );
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
        }
    };

    private final Thread pollerThread = new Thread(poller);

    abstract protected void processBlock(WrapperBlock block);

    public Scanner(WrapperNetwork network, LastBlockPersister lastBlockPersister, Long pollingInterval, Integer commitmentChainLength) {
        this.network = network;
        this.lastBlockPersister = lastBlockPersister;
        this.pollingInterval = pollingInterval;
        this.commitmentChainLength = commitmentChainLength;
    }

    @PostConstruct
    private void open() throws Exception {
        if (transactionsThreshold > 0) {
            pendingTransactionService = new PendingTransactionService(
                    eventPublisher,
                    network.getType(),
                    transactionsThreshold
            );
        }

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
	
	@EventListener
	private void onApplicationLoaded(ContextRefreshedEvent event) {
		pollerThread.start();
		log.info("Subscribed to {} new block event.", network.getType());
	}

    private void loadNextBlock() throws Exception {
        long delta = lastBlockNo - nextBlockNo;
        if (delta <= getCommitmentChainLength()) {
            return;
        }

        long start = System.currentTimeMillis();
        WrapperBlock block = network.getBlock(nextBlockNo);
        if (log.isDebugEnabled()) {
            log.debug("Get next block: {} ms.", System.currentTimeMillis() - start);
        }

        lastBlockIncrementTimestamp = System.currentTimeMillis();

        lastBlockPersister.saveLastBlock(nextBlockNo);
        nextBlockNo++;

        if (pendingTransactionService != null) {
            pendingTransactionService.newBlock(block);
        }
        processBlock(block);
    }

    @PreDestroy
    private void close() {
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
