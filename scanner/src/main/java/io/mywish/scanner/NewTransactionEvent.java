package io.mywish.scanner;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.web3j.protocol.core.methods.response.EthBlock;
import org.web3j.protocol.core.methods.response.Transaction;

@RequiredArgsConstructor
@Getter
public class NewTransactionEvent extends BaseEvent {
    private final EthBlock.Block block;
    private final Transaction transaction;
}
