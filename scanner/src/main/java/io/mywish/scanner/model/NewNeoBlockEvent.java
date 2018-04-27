package io.mywish.scanner.model;

import com.glowstick.neocli4j.Block;
import com.glowstick.neocli4j.TransactionOutput;
import org.springframework.util.MultiValueMap;

public class NewNeoBlockEvent extends BaseEvent {
    public NewNeoBlockEvent(final NetworkType networkType, final Block block, final long blockNumber, MultiValueMap<String, TransactionOutput> addressTransactionOutputs) {
        super(networkType);
    }
}
