package io.mywish.scanner.services.scanners;

import com.glowstick.neocli4j.*;
import io.mywish.scanner.WrapperBlock;
import io.mywish.scanner.WrapperBlockNeo;
import io.mywish.scanner.WrapperTransaction;
import io.mywish.scanner.WrapperTransactionNeo;
import io.mywish.scanner.model.NewBlockEvent;
import io.mywish.scanner.networks.NeoNetwork;
import io.mywish.scanner.services.LastBlockPersister;
import io.mywish.scanner.services.Scanner;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.CollectionUtils;
import org.springframework.util.MultiValueMap;
import java.util.HashMap;
import java.util.List;

@Slf4j
public class NeoScanner extends Scanner {
    private final NeoClient neoClient;

    public NeoScanner(NeoNetwork network, LastBlockPersister lastBlockPersister, Long pollingInterval, Integer commitmentChainLength) {
        super(network, lastBlockPersister, pollingInterval, commitmentChainLength);
        this.neoClient = network.getNeoClient();
    }

    public List<Event> getEvents(String txHash) throws java.io.IOException {
        return neoClient.getEvents(txHash);
    }

    @Override
    protected void loadNextBlock() throws Exception {
        long delta = lastBlockNo - nextBlockNo;
        if (delta <= getCommitmentChainLength()) {
            return;
        }

        long start = System.currentTimeMillis();
        String nextBlockHash = neoClient.getBlockHash(nextBlockNo.intValue());
        Block block = neoClient.getBlock(nextBlockHash);
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
        log.info("{}: new block received {} ({})", network.getType(), blockNo, block.getHash());

        MultiValueMap<String, WrapperTransaction> addressTransactions = CollectionUtils.toMultiValueMap(new HashMap<>());

        if (block.getTransactions() == null) {
            log.warn("{}: block {} has no transactions.", network.getType(), blockNo);
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
                        WrapperTransactionNeo wrapperTransaction = new WrapperTransactionNeo(transaction);
                        addressTransactions.add(output.getAddress(), wrapperTransaction);
                        transaction.getContracts().forEach(contract -> addressTransactions.add(contract, wrapperTransaction));
                    });
//                    eventPublisher.publish(new NewTransactionEvent(networkType, block, output));
                });
        eventPublisher.publish(new NewBlockEvent(network.getType(), new WrapperBlockNeo(block), addressTransactions));
    }
}
