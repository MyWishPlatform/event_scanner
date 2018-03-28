package io.mywish.scanner.model;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.web3j.protocol.core.methods.response.EthBlock;
import org.web3j.protocol.core.methods.response.Transaction;

@Getter
public class NewTransactionEvent extends BaseEvent {
    private final EthBlock.Block block;
    private final Transaction transaction;

    public NewTransactionEvent(NetworkType networkType, EthBlock.Block block, Transaction transaction) {
        super(networkType);
        this.block = block;
        this.transaction = transaction;
    }
}
