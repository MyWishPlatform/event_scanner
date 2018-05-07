package io.mywish.scanner.services.scanners;

import io.mywish.scanner.model.NetworkType;
import io.mywish.scanner.model.NewBlockEvent;
import io.mywish.scanner.model.NewTransactionEvent;
import io.mywish.scanner.services.LastBlockPersister;
import io.mywish.scanner.services.Scanner;
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
import java.util.HashMap;

@Slf4j
public class Web3Scanner extends Scanner {
    private final Web3j web3j;

    public Web3Scanner(NetworkType networkType, Web3j web3j, LastBlockPersister lastBlockPersister, long pollingInterval, int commitmentChainLength) {
        super(networkType, lastBlockPersister, pollingInterval, commitmentChainLength);
        this.web3j = web3j;
    }

    @Override
    protected Long getLastBlock() throws Exception {
        return web3j.ethBlockNumber().send().getBlockNumber().longValue();
    }

    @Override
    protected void loadNextBlock() throws Exception {
        long delta = lastBlockNo - nextBlockNo;
        if (delta <= this.getCommitmentChainLength()) {
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
        nextBlockNo++;

        processBlock(result);
    }

    private void processBlock(EthBlock ethBlock) {
        Block block = ethBlock.getBlock();
        log.info("{}: new bock received {} ({})", networkType, block.getNumber(), block.getHash());

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