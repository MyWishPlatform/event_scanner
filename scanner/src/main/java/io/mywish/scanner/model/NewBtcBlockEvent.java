package io.mywish.scanner.model;

import lombok.Getter;
import org.bitcoinj.core.Block;
import org.bitcoinj.core.Transaction;
import org.springframework.util.MultiValueMap;

@Getter
public class NewBtcBlockEvent extends BaseEvent {
    private final Block block;
    private final long blockNumber;
    private final MultiValueMap<String, Transaction> addressTransactions;

    public NewBtcBlockEvent(final NetworkType networkType, final Block block, final long blockNumber, MultiValueMap<String, Transaction> addressTransactions) {
        super(networkType);
        this.block = block;
        this.blockNumber = blockNumber;
        this.addressTransactions = addressTransactions;
    }
}
