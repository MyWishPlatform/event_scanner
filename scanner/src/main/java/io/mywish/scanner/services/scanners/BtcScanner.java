package io.mywish.scanner.services.scanners;

import com.neemre.btcdcli4j.core.client.BtcdClient;
import io.mywish.scanner.model.NetworkType;
import io.mywish.scanner.model.NewBtcBlockEvent;
import io.mywish.scanner.services.BtcBlockParser;
import io.mywish.scanner.services.LastBlockPersister;
import io.mywish.scanner.services.Scanner;
import lombok.extern.slf4j.Slf4j;
import org.bitcoinj.core.Block;
import org.bitcoinj.core.NetworkParameters;
import org.bitcoinj.core.ScriptException;
import org.bitcoinj.core.TransactionOutput;
import org.bitcoinj.script.Script;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.CollectionUtils;
import org.springframework.util.MultiValueMap;
import java.util.HashMap;

@Slf4j
public class BtcScanner extends Scanner {
    private final NetworkParameters networkParameters;
    private final BtcdClient client;

    @Autowired
    private BtcBlockParser btcBlockParser;

    public BtcScanner(BtcdClient client, NetworkType networkType, LastBlockPersister lastBlockPersister, NetworkParameters networkParameters, Long pollingInterval, Integer commitmentChainLength) {
        super(networkType, lastBlockPersister, pollingInterval, commitmentChainLength);
        this.client = client;
        this.networkParameters = networkParameters;
    }

    @Override
    protected Long getLastBlock() throws Exception {
        return client.getBlockCount().longValue();
    }

    @Override
    protected void loadNextBlock() throws Exception {
        long delta = lastBlockNo - nextBlockNo;
        if (delta <= this.getCommitmentChainLength()) {
            return;
        }

        long start = System.currentTimeMillis();
        String nextBlockHash = client.getBlockHash(nextBlockNo.intValue());
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
