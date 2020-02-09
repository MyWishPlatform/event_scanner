package io.mywish.scanner.model;

import io.lastwill.eventscan.events.model.BaseEvent;
import io.lastwill.eventscan.model.NetworkType;
import io.mywish.blockchain.WrapperBlock;
import io.mywish.blockchain.WrapperTransaction;
import lombok.Getter;
import org.springframework.util.MultiValueMap;

@Getter
public class NewLastBlockEvent extends BaseEvent {
    private final WrapperBlock block;
    private final MultiValueMap<String, WrapperTransaction> transactionsByAddress;

    public NewLastBlockEvent(NetworkType networkType, WrapperBlock block, MultiValueMap<String, WrapperTransaction> transactionsByAddress) {
        super(networkType);
        this.block = block;
        this.transactionsByAddress = transactionsByAddress;
    }
}