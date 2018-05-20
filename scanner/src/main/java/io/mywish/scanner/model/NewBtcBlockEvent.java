package io.mywish.scanner.model;

import io.lastwill.eventscan.model.NetworkType;
import lombok.Getter;
import org.bitcoinj.core.Block;
import org.bitcoinj.core.TransactionOutput;
import org.springframework.util.MultiValueMap;

@Getter
public class NewBtcBlockEvent extends BaseEvent {
    private final Block block;
    private final long blockNumber;
    private final MultiValueMap<String, TransactionOutput> addressTransactionOutputs;

    public NewBtcBlockEvent(final NetworkType networkType, final Block block, final long blockNumber, MultiValueMap<String, TransactionOutput> addressTransactionOutputs) {
        super(networkType);
        this.block = block;
        this.blockNumber = blockNumber;
        this.addressTransactionOutputs = addressTransactionOutputs;
    }
}
