package io.mywish.bot.integration.services;

import io.lastwill.eventscan.model.NetworkType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Optional;

@Component
public class BlockchainExplorerProvider {
    private final HashMap<NetworkType, BlockchainExplorer> explorers = new HashMap<>();
    private BlockchainExplorer stub;

    @Autowired
    public BlockchainExplorerProvider(final List<BlockchainExplorer> blockchainExplorers) {
        for (BlockchainExplorer explorer: blockchainExplorers) {
            if (explorer.getNetworkType() == null && stub != null) {
                stub = explorer;
            }
            BlockchainExplorer previous = explorers.put(explorer.getNetworkType(), explorer);
            if (previous != null) {
                throw new IllegalStateException("Duplicate blockchain explorer for the same network type " + explorer.getNetworkType());
            }
        }
    }

    public BlockchainExplorer get(NetworkType networkType) {
        BlockchainExplorer explorer = explorers.get(networkType);
        if (explorer == null) {
            throw new UnsupportedOperationException("Not supported network type " + networkType);
        }
        return explorer;
    }

    public BlockchainExplorer getOrStub(NetworkType networkType) {
        BlockchainExplorer explorer = explorers.get(networkType);
        if (explorer == null) {
            explorer = stub;
        }
        return explorer;
    }

    public Optional<BlockchainExplorer> getOptional(NetworkType networkType) {
        return Optional.of(explorers.get(networkType));
    }
}
