package io.mywish.scanner.model;

import com.glowstick.neocli4j.Block;
import com.glowstick.neocli4j.TransactionOutput;
import lombok.Getter;
import org.springframework.util.MultiValueMap;

@Getter
public class NewNeoBlockEvent extends BaseEvent {
    private final Block block;
    private final long blockNumber;
    private final MultiValueMap<String, TransactionOutput> addressTransactionOutputs;

    public NewNeoBlockEvent(final NetworkType networkType, final Block block, final long blockNumber, MultiValueMap<String, TransactionOutput> addressTransactionOutputs) {
        super(networkType);
        this.block = block;
        this.blockNumber = blockNumber;
        this.addressTransactionOutputs = addressTransactionOutputs;
    }
}
