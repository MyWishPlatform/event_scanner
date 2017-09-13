package io.lastwill.eventscan.services;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.experimental.var;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.web3j.protocol.core.methods.response.EthBlock;
import org.web3j.protocol.core.methods.response.EthBlock.Block;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

@Component
@Slf4j
public class CommitmentService {
    public enum Status {
        NO_PARENT,
        DUPLICATE,
        OK
    }

    private final int maxChainSize = 64;
    private final ConcurrentHashMap<String, Block> blockChain = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<Long, List<Holder<?>>> register = new ConcurrentHashMap<>();
    private final AtomicLong lowWaiterNo = new AtomicLong(Long.MAX_VALUE);
    private final AtomicLong lowBlockNo = new AtomicLong(Long.MAX_VALUE);
    private final int requiredChainLength;
    private int cleanChainSize = maxChainSize * 2;

    public CommitmentService(@Value("${io.lastwill.eventscan.commit-chain-length}") int requiredChainLength) {
        this.requiredChainLength = requiredChainLength;
    }

    /**
     * Adds block to blockchain.
     *
     * @param block New block.
     * @return True if parent of this black was found in blockchain, and it is not empty blockchain.
     */
    public Status addBlock(EthBlock.Block block) {
        long blockNo = block.getNumber().longValue();
        if (blockChain.containsKey(block.getHash())) {
            return Status.DUPLICATE;
        }
        blockChain.put(block.getHash(), block);
        // update lowest block no
        long low;
        do {
            low = lowBlockNo.get();
            if (low <= blockNo) {
                break;
            }
        }
        while (!lowBlockNo.compareAndSet(low, blockNo));

        // do not skip
//        if (lowWaiterNo.get() >= blockNo) {
//            return;
//        }
        validateChain(block, blockNo);

        if (blockChain.size() > cleanChainSize) {
            cleanup();
            if (blockChain.size() > cleanChainSize) {
                cleanChainSize = blockChain.size() * 2;
                log.info("Increase stored in memory block chain size up to {}.", cleanChainSize);
            }
        }

        if (blockChain.size() == 1 || blockChain.containsKey(block.getParentHash())) {
            return Status.OK;
        }
        else {
            return Status.NO_PARENT;
        }
    }

    public <T> void waitCommitment(String blockHash, long blockNumber, T payload, Handler<T> handler) {
        register.putIfAbsent(blockNumber, new ArrayList<>());

        register.computeIfPresent(blockNumber, (aLong, holders) -> {
            holders.add(new Holder<T>(blockHash, payload, handler));
            return holders;
        });

        long low;
        do {
            low = lowWaiterNo.get();
            if (low <= blockNumber) {
                break;
            }
        }
        while (!lowWaiterNo.compareAndSet(low, blockNumber));
    }

    public <T> CompletionStage<Boolean> waitCommitment(String blockHash, long blockNumber) {
        final CompletableFuture<Boolean> completableFuture = new CompletableFuture<>();
        waitCommitment(blockHash, blockNumber, null, new Handler<T>() {
            @Override
            public void committed(long blockNumber, T payload, int chainLength) {
                completableFuture.complete(true);
            }

            @Override
            public void rejected(long blockNumber, T payload, int chainLength) {
                completableFuture.complete(false);
            }
        });
        return completableFuture;
    }

    private void validateChain(Block childBlock, long initialBlockNo) {
        var block = blockChain.get(childBlock.getParentHash());
        if (block == null) {
            if (lowWaiterNo.get() < childBlock.getNumber().longValue()) {
                log.error("Broken chain on level {}, but there is lowest waiters at {}.", childBlock.getNumber().longValue(), lowWaiterNo.get());
            }
            return;
        }
        long blockNo = block.getNumber().longValue();
        int delta = (int) (initialBlockNo - blockNo);
        if (delta < requiredChainLength) {
            // just go deeper
            validateChain(block, initialBlockNo);
            return;
        }
        lowWaiterNo.compareAndSet(blockNo, blockNo + 1);
        List<Holder<?>> holders = register.remove(blockNo);
        if (holders != null) {
            for (Holder<?> holder : holders) {
                if (holder.blockHash.equals(block.getHash())) {
                    holder.commit(blockNo, delta);
                }
                else {
                    holder.reject(blockNo, delta);
                }
            }
        }

        if (blockNo > lowWaiterNo.get()) {
            validateChain(block, initialBlockNo);
            lowWaiterNo.compareAndSet(blockNo, blockNo + 1);
        }
    }

    private void cleanup() {
        long lowBlock = Math.max(lowWaiterNo.get() - maxChainSize, 1);
        blockChain.values()
                .stream()
                .filter(block -> block.getNumber().longValue() <= lowBlock)
                .map(Block::getHash)
                .collect(Collectors.toList())
                .forEach(blockChain::remove);
    }

    public interface Handler<T> {
        void committed(long blockNumber, T payload, int chainLength);

        void rejected(long blockNumber, T payload, int chainLength);
    }

    @RequiredArgsConstructor
    private class Holder<T> {
        private final String blockHash;
        private final T payload;
        private final Handler<T> handler;

        void commit(long blockNumber, int chainLength) {
            handler.committed(blockNumber, payload, chainLength);
        }

        void reject(long blockNumber, int chainLength) {
            handler.rejected(blockNumber, payload, chainLength);
        }
    }

    @Getter
    @RequiredArgsConstructor
    public class Result<T> {
        private final T payload;
        private final boolean committed;
    }
}
