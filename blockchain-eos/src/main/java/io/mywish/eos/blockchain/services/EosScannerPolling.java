package io.mywish.eos.blockchain.services;

import io.lastwill.eventscan.events.model.BaseEvent;
import io.mywish.blockchain.WrapperBlock;
import io.mywish.scanner.services.LastBlockPersister;
import io.mywish.scanner.services.ScannerPolling;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.atomic.AtomicInteger;

import static io.mywish.eos.blockchain.util.NewBlockEventUtil.createBlockEvent;
import static io.mywish.eos.blockchain.util.NewBlockEventUtil.createPendingEvent;

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

        BaseEvent event = isPending
                ? createPendingEvent(network, block)
                : createBlockEvent(network, block);
        eventPublisher.publish(event);
    }
}
