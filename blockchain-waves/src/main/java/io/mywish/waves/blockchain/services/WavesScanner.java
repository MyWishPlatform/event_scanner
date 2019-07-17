package io.mywish.waves.blockchain.services;

import io.mywish.blockchain.WrapperBlock;
import io.mywish.blockchain.WrapperTransaction;
import io.mywish.scanner.model.NewBlockEvent;
import io.mywish.scanner.services.LastBlockPersister;
import io.mywish.scanner.services.ScannerPolling;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.CollectionUtils;
import org.springframework.util.MultiValueMap;

import java.util.HashMap;
import java.util.concurrent.atomic.AtomicInteger;

@Slf4j
public class WavesScanner extends ScannerPolling {
    private final AtomicInteger counter = new AtomicInteger(0);

    public WavesScanner(WavesNetwork network, LastBlockPersister lastBlockPersister, Long pollingInterval, Integer commitmentChainLength, Long reachInterval) {
        super(network, lastBlockPersister, pollingInterval, commitmentChainLength, reachInterval);
    }

    public WavesScanner(WavesNetwork network, LastBlockPersister lastBlockPersister, Long pollingInterval, Integer commitmentChainLength) {
        super(network, lastBlockPersister, pollingInterval, commitmentChainLength);
    }

    @Override
    protected void processBlock(WrapperBlock block) {
        log.info("{}: new block received {} ({})", network.getType(), block.getNumber(), block.getHash());

        MultiValueMap<String, WrapperTransaction> addressTransactions = CollectionUtils.toMultiValueMap(new HashMap<>());

        if (block.getTransactions() == null) {
            log.warn("{}: block {} has no transactions.", network.getType(), block.getNumber());
            return;
        }
        block.getTransactions()
                .forEach(transaction -> {
                    transaction.getInputs().forEach(input -> addressTransactions.add(input.toLowerCase(), transaction));
                    transaction.getOutputs().forEach(output -> addressTransactions.add(output.getAddress().toLowerCase(), transaction));
//                    eventPublisher.publish(new NewTransactionEvent(networkType, block, output));
                });
        eventPublisher.publish(new NewBlockEvent(network.getType(), block, addressTransactions));
    }
}
