package io.mywish.eos.blockchain.services;

import io.mywish.blockchain.WrapperBlock;
import io.mywish.blockchain.WrapperTransaction;
import io.mywish.scanner.model.NewBlockEvent;
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
    private final Runnable blockReceiver = () -> {
        do {

            try {
                getEosNetwork().subscribe(lastBlockPersister.getLastBlock(), block -> {
                    lastBlockPersister.saveLastBlock(block.getNumber());
                    processBlock(block);
                });
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
    };

    public EosScanner(EosNetwork network, LastBlockPersister lastBlockPersister) {
        super(network, lastBlockPersister);
        this.setWorker(blockReceiver);
    }

    @Override
    protected void processBlock(WrapperBlock block) {
        MultiValueMap<String, WrapperTransaction> addressTransactions = CollectionUtils.toMultiValueMap(new HashMap<>());

        block.getTransactions().forEach(tx -> {
            tx.getInputs()
                    .forEach(address -> {
                        addressTransactions.add(address, tx);
                    });

            tx.getOutputs()
                    .forEach(wrapperOutput -> {
                        addressTransactions.add(wrapperOutput.getAddress(), tx);
                    });
        });
        eventPublisher.publish(new NewBlockEvent(network.getType(), block, addressTransactions));
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
        }
        catch (Exception e) {
            log.warn("Persister for {} closing failed.", network.getType(), e);
        }
    }
}
