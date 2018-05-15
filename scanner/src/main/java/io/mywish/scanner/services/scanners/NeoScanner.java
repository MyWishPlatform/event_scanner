package io.mywish.scanner.services.scanners;

import com.glowstick.neocli4j.*;
import io.mywish.scanner.model.NetworkType;
import io.mywish.scanner.model.NewNeoBlockEvent;
import io.mywish.scanner.services.LastBlockPersister;
import io.mywish.scanner.services.Scanner;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.CollectionUtils;
import org.springframework.util.MultiValueMap;
import java.util.HashMap;
import java.util.List;

@Slf4j
public class NeoScanner extends Scanner {
    private final NeoClient client;

    public NeoScanner(NeoClient client, NetworkType networkType, LastBlockPersister lastBlockPersister, Long pollingInterval, Integer commitmentChainLength) {
        super(networkType, lastBlockPersister, pollingInterval, commitmentChainLength);
        this.client = client;
    }

    public List<Event> getEvents(String txHash) throws java.io.IOException {
        return client.getEvents(txHash);
    }

    @Override
    protected Long getLastBlock() throws Exception {
        return client.getBlockCount().longValue();
    }

    @Override
    protected void loadNextBlock() throws Exception {
        long delta = lastBlockNo - nextBlockNo;
        if (delta <= getCommitmentChainLength()) {
            return;
        }

        long start = System.currentTimeMillis();
        String nextBlockHash = client.getBlockHash(nextBlockNo.intValue());
        Block block = client.getBlock(nextBlockHash);
        if (log.isDebugEnabled()) {
            log.debug("Get next block: {} ms.", System.currentTimeMillis() - start);
        }

        lastBlockIncrementTimestamp = System.currentTimeMillis();

        lastBlockPersister.saveLastBlock(nextBlockNo);
        long blockNo = nextBlockNo;
        nextBlockNo++;

        processBlock(block, blockNo);
    }

    private void processBlock(Block block, long blockNo) {
        log.info("{}: new block received {} ({})", networkType, blockNo, block.getHash());

        MultiValueMap<String, TransactionOutput> addressTransactions = CollectionUtils.toMultiValueMap(new HashMap<>());

        if (block.getTransactions() == null) {
            log.warn("{}: block {} has no transactions.", networkType, blockNo);
            return;
        }
        block.getTransactions()
                .forEach(transaction -> {
                    transaction.getOutputs().forEach(output -> {
//                        Script script;
//                        try {
//                            script = output.getScriptPubKey();
//                        }
//                        catch (ScriptException ex) {
//                            log.warn("Skip output with script error: ", output, ex);
//                            return;
//                        }
//                        if (!script.isSentToAddress() && !script.isPayToScriptHash() && !script.isSentToRawPubKey()) {
//                            log.debug("Skip output with not appropriate script {}.", script);
//                            return;
//                        }
                        String address = output.getAddress();
                        addressTransactions.add(address, output);
                    });
//                    eventPublisher.publish(new NewTransactionEvent(networkType, block, output));
                });
        eventPublisher.publish(new NewNeoBlockEvent(networkType, block, blockNo, addressTransactions));
    }
}
