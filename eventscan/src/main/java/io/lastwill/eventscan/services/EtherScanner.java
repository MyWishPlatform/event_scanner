package io.lastwill.eventscan.services;

import io.lastwill.eventscan.events.NewBlockEvent;
import io.lastwill.eventscan.events.NewTransactionEvent;
import io.lastwill.eventscan.exceptions.Web3Exception;
import lombok.experimental.var;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import org.springframework.util.MultiValueMap;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.DefaultBlockParameterNumber;
import org.web3j.protocol.core.Response;
import org.web3j.protocol.core.methods.response.*;
import org.web3j.protocol.core.methods.response.EthBlock.Block;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.io.*;
import java.util.HashMap;

@Slf4j
@Component
public class EtherScanner {
    public static final long INFO_INTERVAL = 60000;
    public static final long WARN_INTERVAL = 120000;
    @Autowired
    private Web3j web3j;

    @Autowired
    private EventPublisher eventPublisher;

    @Autowired
    private LastBlockPersister lastBlockPersister;

    @Value("${io.lastwill.eventscan.polling-interval-ms}")
    private long pollingInterval;

    @Value("${io.lastwill.eventscan.commit-chain-length}")
    private int commitmentChainLength;

    private Long lastBlockNo;
    private Long nextBlockNo;
    private long lastBlockIncrementTimestamp;


    private Runnable poller = new Runnable() {
        @Override
        public void run() {
            while (true) {
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
                        log.warn("There is no block from {} ms!", interval);
                    }
                    else if (interval > INFO_INTERVAL) {
                        log.info("There is no block from {} ms.", interval);
                    }

                    log.debug("All blocks processed, wait new one.");
                    Thread.sleep(pollingInterval);
                }
                catch (InterruptedException e) {
                    log.warn("Polling cycle was interrupted.", e);
                    break;
                }
                catch (Throwable e) {
                    log.error("Exception handled in polling cycle. Continue.", e);
                    try {
                        Thread.sleep(pollingInterval);
                    }
                    catch (InterruptedException e1) {
                        log.warn("Polling cycle was interrupted after error.", e1);
                        break;
                    }
                }
            }
        }
    };

    @PostConstruct
    protected void init() throws IOException {
        nextBlockNo = lastBlockPersister.getLastBlock();
        try {
            boolean syncing = web3j.ethSyncing().send().isSyncing();
            lastBlockNo = web3j.ethBlockNumber().send().getBlockNumber().longValue();
            lastBlockIncrementTimestamp = System.currentTimeMillis();
            if (nextBlockNo == null) {
                nextBlockNo = lastBlockNo - commitmentChainLength;
            }
            log.info("Web3 syncing status: {}, latest block is {} but next is {}.", syncing ? "syncing" : "synced", lastBlockNo, nextBlockNo);
//            blockFilter = web3j.ethNewBlockFilter().send();
//            log.info("Block filter was created with id {}.", blockFilter.getFilterId());
        }
        catch (IOException e) {
            log.error("Web3 sending failed.");
            throw e;
        }

        new Thread(poller).start();
        log.info("Subscribed to web3 new block event.");
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

    private void processBlock(EthBlock ethBlock) throws Exception {
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
                        log.debug("Empty to field for transaction {}. Skip it.", transaction.getHash());
                    }
                    eventPublisher.publish(new NewTransactionEvent(block, transaction));
                });

        eventPublisher.publish(new NewBlockEvent(block, addressTransactions));
    }
}
