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
import org.web3j.protocol.core.methods.response.EthBlock;
import org.web3j.protocol.core.methods.response.EthFilter;
import org.web3j.protocol.core.methods.response.EthLog;
import org.web3j.protocol.core.methods.response.Transaction;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.util.HashMap;

@Slf4j
@Component
public class EtherScanner {
    @Autowired
    private Web3j web3j;

    @Autowired
    private EventPublisher eventPublisher;

    @Autowired
    private CommitmentService commitmentService;

//    @Value("${io.lastwill.eventscan.start-block:#{null}}")
//    private Long nextBlock;

    @Value("${io.lastwill.eventscan.polling-interval-ms}")
    private long pollingInterval;

    private EthFilter blockFilter;
    private Long repeatBranch;

    private Runnable poller = new Runnable() {
        @Override
        public void run() {
            while (true) {
                try {
                    if (repeatBranch != null) {
                        processBlockNumber(repeatBranch);
                        if (repeatBranch != null) {
                            repeatBranch ++;
                            continue;
                        }
                    }

                    long start = System.currentTimeMillis();
                    EthLog ethLog = web3j.ethGetFilterChanges(blockFilter.getFilterId())
                            .send();
                    if (log.isDebugEnabled()) {
                        log.debug("Get filter logs: {} ms", System.currentTimeMillis() - start);
                    }
                    if (ethLog == null) {
                        throw new Exception("ethGetFilterLogs returns null.");
                    }
                    if (ethLog.hasError()) {
                        throw new Web3Exception(ethLog.getError());
                    }
                    if (ethLog.getLogs() == null) {
                        throw new Exception("ethGetFilterLogs returns null list.");
                    }

                    for (EthLog.LogResult logResult : ethLog.getLogs()) {
                        EthLog.Hash hash = (EthLog.Hash) logResult;
                        String blockHash = hash.get();
                        processBlockHash(blockHash);
                    }

                    if (ethLog.getLogs().isEmpty()) {
                        Thread.sleep(pollingInterval);
                    }


//                    EthBlock result = web3j.ethGetBlockByNumber(
//                            new DefaultBlockParameterNumber(nextBlock),
//                            true
//                    ).send();
//
//
//                    if (result == null) {
//                        throw new Exception("Result message contains null.");
//                    }
//
//                    if (result.getBlock() == null) {
//                        long lastBlockNo = web3j.ethBlockNumber().send().getBlockNumber().longValue();
//                        if (lastBlockNo < nextBlock) {
//                            Thread.sleep(pollingInterval);
//                            continue;
//                        }
//                        else {
//                            throw new Exception("Block message has null block!");
//                        }
//                    }
//
//                    processBlock(result);
//                    nextBlock++;
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
        try {
            boolean syncing = web3j.ethSyncing().send().isSyncing();
            long lastBlockNo = web3j.ethBlockNumber().send().getBlockNumber().longValue();
//            if (nextBlock == null) {
//                nextBlock = lastBlockNo;
//            }
            log.info("Web3 syncing status: {}, latest block is {}.", syncing ? "syncing" : "synced", lastBlockNo);
            blockFilter = web3j.ethNewBlockFilter().send();
            log.info("Block filter was created with id {}.", blockFilter.getFilterId());
        }
        catch (IOException e) {
            log.error("Web3 sending failed.");
            throw e;
        }


        new Thread(poller).start();
//        web3j.blockObservable(true)
//                .subscribe(this::processBlockSafe);
        log.info("Subscribed to web3 new block event.");
    }

    private void processBlockHash(String blockHash) {
        try {
            long start = System.currentTimeMillis();
            log.debug("Get block by hash {}.", blockHash);
            EthBlock result = web3j.ethGetBlockByHash(blockHash, true)
                    .send();
            if (log.isDebugEnabled()) {
                log.debug("Get block by hash: {} ms.", System.currentTimeMillis() - start);
            }

            processBlock(result);
        }
        catch (Throwable e) {
            log.error("Error on processing new block.", e);
        }
    }

    private void processBlockNumber(long blockNo) {
        try {
            long start = System.currentTimeMillis();
            log.debug("Get block by number {}.", blockNo);
            EthBlock result = web3j.ethGetBlockByNumber(new DefaultBlockParameterNumber(blockNo), true)
                    .send();
            if (log.isDebugEnabled()) {
                log.debug("Get block by number: {} ms.", System.currentTimeMillis() - start);
            }

            if (result.getBlock() == null) {
                log.warn("Result has null block when it was received by number {}.", blockNo);
                repeatBranch = null;
                return;
            }

            processBlock(result);
        }
        catch (Throwable e) {
            log.error("Error on processing new block.", e);
        }
    }

    private void processBlock(EthBlock ethBlock) throws Exception {
        if (ethBlock.hasError()) {
            Response.Error error = ethBlock.getError();
            throw new Exception("Block with error with code " + error.getCode() + ", message '" + error.getMessage() + "' and data " + error.getData());
        }
        EthBlock.Block block = ethBlock.getBlock();
        log.debug("New bock received {} ({})", block.getNumber(), block.getHash());

        var addingBlockStatus = commitmentService.addBlock(block);
        switch (addingBlockStatus) {
            case DUPLICATE:
                log.warn("Duplicate block with hash {} ({}). Skip it.", block.getHash(), block.getNumber());
                repeatBranch = null;
                break;
            case NO_PARENT:
                log.info("Block {} without parent found.", block.getNumber());
                repeatBranch = block.getNumber().longValue() - 1;
                break;
        }

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
