package io.mywish.eos.blockchain.services;

import io.lastwill.eventscan.events.model.BaseEvent;
import io.mywish.blockchain.WrapperBlock;
import io.mywish.blockchain.WrapperTransaction;
import io.mywish.scanner.model.NewBlockEvent;
import io.mywish.scanner.model.NewPendingTransactionsEvent;
import io.mywish.scanner.services.LastBlockPersister;
import io.mywish.scanner.services.ScannerPolling;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.CollectionUtils;
import org.springframework.util.MultiValueMap;

import java.util.HashMap;
import java.util.concurrent.atomic.AtomicInteger;

@Slf4j
public class EosScannerPolling extends ScannerPolling {
    private final AtomicInteger blockCounter = new AtomicInteger(0);
    private final boolean isPending;

    public EosScannerPolling(final EosNetwork network, final LastBlockPersister lastBlockPersister, long pollingInterval, int commitmentChainLength, final boolean isPending) {
        super(network, lastBlockPersister, pollingInterval, commitmentChainLength);
        this.isPending = isPending;
    }

    @Override
    protected void processBlock(WrapperBlock block) {
        if (blockCounter.incrementAndGet() == 10) {
            log.info("{}: 10 blocks received, the last {} ({})",
                    network.getType() + (isPending ? " (pending)" : ""),
                    block.getNumber(),
                    block.getHash());

            blockCounter.set(0);
        }

        BaseEvent event = isPending ? createPendingEvent(block) : createBlockEvent(block);
        eventPublisher.publish(event);
    }

    private NewBlockEvent createBlockEvent(WrapperBlock block) {
        MultiValueMap<String, WrapperTransaction> addressTransactions = CollectionUtils.toMultiValueMap(new HashMap<>());

        block.getTransactions().forEach(tx -> {
            tx.getInputs()
                    .forEach(address -> addressTransactions.add(address, tx));

            tx.getOutputs()
                    .forEach(wrapperOutput -> addressTransactions.add(wrapperOutput.getAddress(), tx));
        });
        return new NewBlockEvent(network.getType(), block, addressTransactions);
    }

    private NewPendingTransactionsEvent createPendingEvent(WrapperBlock block) {
        return new NewPendingTransactionsEvent(
                network.getType(),
                block.getTransactions()
        );
    }
}
