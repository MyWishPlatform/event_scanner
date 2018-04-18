package io.mywish.scanner.services;

import com.neemre.btcdcli4j.core.client.BtcdClient;
import io.mywish.scanner.model.NetworkType;
import io.mywish.scanner.model.NewBtcBlockEvent;
import lombok.extern.slf4j.Slf4j;
import org.bitcoinj.core.Block;
import org.bitcoinj.core.NetworkParameters;
import org.bitcoinj.core.ScriptException;
import org.bitcoinj.core.TransactionOutput;
import org.bitcoinj.script.Script;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.util.CollectionUtils;
import org.springframework.util.MultiValueMap;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.util.HashMap;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicBoolean;

@Slf4j
public class BtcScanner {
    public static final long INFO_INTERVAL = 600000;
    public static final long WARN_INTERVAL = 1200000;

    private final NetworkType networkType;
    private final LastBlockPersister lastBlockPersister;
    private final NetworkParameters networkParameters;
    private final AtomicBoolean isTerminated = new AtomicBoolean(false);

    private final BtcdClient client;
    @Autowired
    private EventPublisher eventPublisher;
    @Autowired
    private BtcBlockParser btcBlockParser;
    @Value("${etherscanner.bitcoin.polling-interval-ms}")
    private long pollingInterval;
    @Value("${etherscanner.bitcoin.commit-chain-length}")
    private int commitmentChainLength;

    private Integer lastBlockNo;
    private Integer nextBlockNo;
    private long lastBlockIncrementTimestamp;

    private final Object sync = new Object();

    private final Runnable poller = new Runnable() {
        @Override
        public void run() {
            while (!isTerminated.get()) {
                try {
                    long start = System.currentTimeMillis();
                    lastBlockNo = client.getBlockCount();
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

    public BtcScanner(BtcdClient client, NetworkType networkType, LastBlockPersister lastBlockPersister, NetworkParameters networkParameters) {
        this.client = client;
        this.networkType = networkType;
        this.lastBlockPersister = lastBlockPersister;
        this.networkParameters = networkParameters;
    }

    @PostConstruct
    public void open() throws Exception {
        lastBlockPersister.open();
        nextBlockNo = Optional
                .ofNullable(lastBlockPersister.getLastBlock())
                .map(Long::intValue)
                .orElse(null);
        try {
            lastBlockNo = client.getBlockCount();
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

    private void loadNextBlock() throws Exception {
        long delta = lastBlockNo - nextBlockNo;
        if (delta <= commitmentChainLength) {
            return;
        }

        long start = System.currentTimeMillis();
        String nextBlockHash = client.getBlockHash(nextBlockNo);
        String rowBlock = (String) client.getBlock(nextBlockHash, false);
        if (log.isDebugEnabled()) {
            log.debug("Get next block: {} ms.", System.currentTimeMillis() - start);
        }

        if (rowBlock == null || rowBlock.isEmpty()) {
            throw new Exception("Empty result with row block!");
        }

        Block block = btcBlockParser.parse(networkParameters, rowBlock);

        lastBlockIncrementTimestamp = System.currentTimeMillis();

        lastBlockPersister.saveLastBlock(nextBlockNo);
        long blockNo = nextBlockNo;
        nextBlockNo++;

        processBlock(block, blockNo);
    }

    private void processBlock(Block block, long blockNo) {
        log.info("{}: new bock received {} ({})", networkType, blockNo, block.getHash());

        MultiValueMap<String, TransactionOutput> addressTransactions = CollectionUtils.toMultiValueMap(new HashMap<>());

        if (block.getTransactions() == null) {
            log.warn("{}: block {} has not transactions.", networkType, blockNo);
            return;
        }
        block.getTransactions()
                .forEach(transaction -> {
                    transaction.getOutputs().forEach(output -> {
                        Script script;
                        try {
                            script = output.getScriptPubKey();
                        }
                        catch (ScriptException ex) {
                            log.warn("Skip output with script error: ", output, ex);
                            return;
                        }
                        if (!script.isSentToAddress() && !script.isPayToScriptHash() && !script.isSentToRawPubKey()) {
                            log.debug("Skip output with not appropriate script {}.", script);
                            return;
                        }
                        String address = script
                                .getToAddress(networkParameters, true)
                                .toBase58();
                        addressTransactions.add(address, output);
                    });
//                    eventPublisher.publish(new NewTransactionEvent(networkType, block, output));
                });

        eventPublisher.publish(new NewBtcBlockEvent(networkType, block, blockNo, addressTransactions));
    }
}
