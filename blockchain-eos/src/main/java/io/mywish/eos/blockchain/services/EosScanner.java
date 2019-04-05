package io.mywish.eos.blockchain.services;

import io.lastwill.eventscan.events.model.BaseEvent;
import io.mywish.blockchain.WrapperBlock;
import io.mywish.eoscli4j.model.BlockReliability;
import io.mywish.scanner.services.LastBlockPersister;
import io.mywish.scanner.services.Scanner;
import lombok.extern.slf4j.Slf4j;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.util.concurrent.atomic.AtomicBoolean;

import static io.mywish.eos.blockchain.util.NewBlockEventUtil.createBlockEvent;
import static io.mywish.eos.blockchain.util.NewBlockEventUtil.createPendingEvent;

@Slf4j
public class EosScanner extends Scanner {
    private final static long SUBSCRIPTION_REPEAT_SEC = 10;
    private final AtomicBoolean terminated = new AtomicBoolean(false);
    private final Object sync = new Object();
    private final boolean isPending;

    public EosScanner(final EosNetwork network, final LastBlockPersister lastBlockPersister, final boolean isPending) {
        super(network, lastBlockPersister);
        this.isPending = isPending;
        this.setWorker(() -> {
            do {

                try {
                    getEosNetwork().subscribe(
                            lastBlockPersister.getLastBlock(),
                            block -> {
                                lastBlockPersister.saveLastBlock(block.getNumber());
                                processBlock(block);
                            },
                            isPending ? BlockReliability.REVERSIBLE : BlockReliability.IRREVERSIBLE);
                } catch (Exception e) {
                    log.error("Subscription aborted with error. Repeat after {} seconds.", SUBSCRIPTION_REPEAT_SEC, e);
                    synchronized (sync) {
                        try {
                            sync.wait(SUBSCRIPTION_REPEAT_SEC * 1000);
                        } catch (InterruptedException ex) {
                            log.error("Waiting after error was interrupted.", ex);
                            break;
                        }
                    }
                }
            }
            while (!terminated.get());
        });
    }

    @Override
    protected void processBlock(WrapperBlock block) {
        BaseEvent event = isPending
                ? createPendingEvent(network, block)
                : createBlockEvent(network, block);
        eventPublisher.publish(event);
    }

    protected EosNetwork getEosNetwork() {
        return (EosNetwork) network;
    }

    @PostConstruct
    @Override
    protected void open() {
        lastBlockPersister.open();
    }

    @PreDestroy
    @Override
    protected void close() {
        terminated.set(true);
        getEosNetwork().close();
        log.info("Wait {} ms till cycle is completed for {}.", SUBSCRIPTION_REPEAT_SEC + 1, network.getType());
        synchronized (sync) {
            sync.notifyAll();
        }

        try {
            lastBlockPersister.close();
        } catch (Exception e) {
            log.warn("Persister for {} closing failed.", network.getType(), e);
        }
    }
}
