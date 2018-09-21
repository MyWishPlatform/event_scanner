package io.mywish.scanner.model;

import io.lastwill.eventscan.events.model.BaseEvent;
import io.lastwill.eventscan.model.NetworkType;
import io.mywish.blockchain.WrapperBlock;
import io.mywish.blockchain.WrapperTransaction;
import lombok.Getter;

@Getter
public class NewTransactionEvent extends BaseEvent {
    private final WrapperBlock block;
    private final WrapperTransaction transaction;

    public NewTransactionEvent(NetworkType networkType, WrapperBlock block, WrapperTransaction transaction) {
        super(networkType);
        this.block = block;
        this.transaction = transaction;
    }
}
