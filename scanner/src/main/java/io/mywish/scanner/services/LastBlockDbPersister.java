package io.mywish.scanner.services;

import io.lastwill.eventscan.model.LastBlock;
import io.lastwill.eventscan.model.NetworkType;
import io.lastwill.eventscan.repositories.LastBlockRepository;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class LastBlockDbPersister implements LastBlockPersister {
    private final NetworkType networkType;
    private final LastBlockRepository lastBlockRepository;
    private Long lastBlockNumber;

    public LastBlockDbPersister(
            @NonNull NetworkType networkType,
            @NonNull LastBlockRepository lastBlockRepository,
            Long lastBlock
    ) {
        this.networkType = networkType;
        this.lastBlockRepository = lastBlockRepository;
        this.lastBlockNumber = lastBlock;
    }

    @Override
    public void open() {
        if (lastBlockNumber != null) {
            saveLastBlock(lastBlockNumber);
        } else {
            lastBlockNumber = lastBlockRepository.getLastBlockForNetwork(networkType);
        }
    }

    @Override
    public void close() {
    }

    @Override
    public Long getLastBlock() {
        return lastBlockNumber;
    }

    @Override
    public synchronized void saveLastBlock(long blockNumber) {
        if (lastBlockRepository.getLastBlockForNetwork(networkType) == null) {
            lastBlockRepository.save(new LastBlock(networkType, blockNumber));
        } else {
            lastBlockRepository.updateLastBlock(networkType, blockNumber);
        }
        lastBlockNumber = blockNumber;
    }
}
