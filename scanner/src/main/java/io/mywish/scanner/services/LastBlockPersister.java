package io.mywish.scanner.services;

public interface LastBlockPersister {
    void open();

    void close();

    Long getLastBlock();

    void saveLastBlock(long blockNumber);
}
