package io.mywish.scanner.services;

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicLong;

public class LastBlockMemoryPersister implements LastBlockPersister {
    private final AtomicBoolean isLastBlockEmpty = new AtomicBoolean(true);
    private final AtomicLong lastBlock = new AtomicLong(0);

    public LastBlockMemoryPersister(Long lastBlock) {
        if (lastBlock == null) {
            isLastBlockEmpty.set(true);
        }
        else {
            this.lastBlock.set(lastBlock);
        }
    }

    @Override
    public void open() {

    }

    @Override
    public void close() {

    }

    @Override
    public Long getLastBlock() {
        if (isLastBlockEmpty.get()) {
            return null;
        }
        return lastBlock.get();
    }

    @Override
    public void saveLastBlock(long blockNumber) {
        lastBlock.set(blockNumber);
    }
}
