package io.mywish.scanner.model;

import io.mywish.scanner.WrapperBlock;
import io.mywish.scanner.WrapperTransaction;
import lombok.Getter;
import org.springframework.util.MultiValueMap;

@Getter
public class NewBlockEvent extends BaseEvent {
    private final WrapperBlock block;
    private final MultiValueMap<String, WrapperTransaction> transactionsByAddress;

    public NewBlockEvent(NetworkType networkType, WrapperBlock block, MultiValueMap<String, WrapperTransaction> transactionsByAddress) {
        super(networkType);
        this.block = block;
        this.transactionsByAddress = transactionsByAddress;
    }
}