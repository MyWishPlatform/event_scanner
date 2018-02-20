package io.mywish.scanner;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.util.MultiValueMap;
import org.web3j.protocol.core.methods.response.EthBlock;
import org.web3j.protocol.core.methods.response.Transaction;

@RequiredArgsConstructor
@Getter
public class NewBlockEvent extends BaseEvent {
    private final EthBlock.Block block;
    private final MultiValueMap<String, Transaction> transactionsByAddress;
}
