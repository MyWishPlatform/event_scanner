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
            try {
                lastBlockNumber = lastBlockRepository.getLastBlockForNetwork(networkType);
            } catch (Throwable e) {
                log.warn("Impossible to read last block from database.", e);
            }
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
    public void saveLastBlock(long blockNumber) {
        if (lastBlockNumber == null) {
            lastBlockRepository.save(new LastBlock(networkType, blockNumber));
        } else {
            lastBlockRepository.updateLastBlock(networkType, blockNumber);
        }
        lastBlockNumber = blockNumber;
    }
}
