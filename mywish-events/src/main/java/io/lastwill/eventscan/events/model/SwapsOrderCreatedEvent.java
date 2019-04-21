package io.lastwill.eventscan.events.model;

import io.lastwill.eventscan.model.NetworkType;
import io.lastwill.eventscan.model.ProductSwaps2;
import io.mywish.blockchain.WrapperTransaction;
import lombok.Getter;

@Getter
public class SwapsOrderCreatedEvent extends BaseEvent {
    private final ProductSwaps2 product;
    private final WrapperTransaction transaction;

    public SwapsOrderCreatedEvent(NetworkType networkType, ProductSwaps2 product, WrapperTransaction transaction) {
        super(networkType);
        this.product = product;
        this.transaction = transaction;
    }
}
