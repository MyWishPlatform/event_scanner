package io.mywish.scanner.model;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.util.MultiValueMap;
import org.web3j.protocol.core.methods.response.EthBlock;
import org.web3j.protocol.core.methods.response.Transaction;

@Getter
public class NewWeb3BlockEvent extends BaseEvent {
    private final EthBlock.Block block;
    private final MultiValueMap<String, Transaction> transactionsByAddress;

    public NewWeb3BlockEvent(NetworkType networkType, EthBlock.Block block, MultiValueMap<String, Transaction> transactionsByAddress) {
        super(networkType);
        this.block = block;
        this.transactionsByAddress = transactionsByAddress;
    }
}
