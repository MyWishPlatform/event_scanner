package io.mywish.eos.blockchain.services;

import io.lastwill.eventscan.events.model.BaseEvent;
import io.mywish.blockchain.WrapperBlock;
import io.mywish.blockchain.WrapperTransaction;
import io.mywish.eoscli4j.model.BlockReliability;
import io.mywish.scanner.model.NewBlockEvent;
import io.mywish.scanner.model.NewPendingTransactionsEvent;
import io.mywish.scanner.services.LastBlockPersister;
import io.mywish.scanner.services.Scanner;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.CollectionUtils;
import org.springframework.util.MultiValueMap;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.util.HashMap;
import java.util.concurrent.atomic.AtomicBoolean;

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
                }
                catch (Exception e) {
                    log.error("Subscription aborted with error. Repeat after {} seconds.", SUBSCRIPTION_REPEAT_SEC, e);
                    synchronized (sync) {
                        try {
                            sync.wait(SUBSCRIPTION_REPEAT_SEC * 1000);
                        }
                        catch (InterruptedException ex) {
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
        BaseEvent event = isPending ? createPendingEvent(block) : createBlockEvent(block);
        eventPublisher.publish(event);
    }

    protected EosNetwork getEosNetwork() {
        return (EosNetwork) network;
    }

    @PostConstruct
    @Override
    protected void open() {
        if (isPending) {

        }
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
        }
        catch (Exception e) {
            log.warn("Persister for {} closing failed.", network.getType(), e);
        }
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
