package io.mywish.scanner.services.scanners;

import io.mywish.scanner.services.LastBlockPersister;
import io.mywish.scanner.services.Scanner;
import io.mywish.wrapper.WrapperBlock;
import io.mywish.wrapper.networks.EosNetwork;
import lombok.extern.slf4j.Slf4j;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

@Slf4j
public class EosScanner extends Scanner {
    private final Runnable blockReceiver = () -> {
        try {
            ((EosNetwork) network).subscribe(network.getLastBlock(), block -> {
                lastBlockPersister.saveLastBlock(block.getNumber());
                processBlock(block);
            });
        } catch (Exception e) {

        }
    };

    public EosScanner(EosNetwork network, LastBlockPersister lastBlockPersister) {
        super(network, lastBlockPersister);
        this.setWorker(blockReceiver);
    }

    @Override
    protected void processBlock(WrapperBlock block) {
        System.out.println(block.getNumber());
    }

    @PostConstruct
    @Override
    protected void open() {
        lastBlockPersister.open();
    }

    @PreDestroy
    @Override
    protected void close() {
        try {
            lastBlockPersister.close();
        }
        catch (Exception e) {
            log.warn("Persister for {} closing failed.", network.getType(), e);
        }
    }
}
