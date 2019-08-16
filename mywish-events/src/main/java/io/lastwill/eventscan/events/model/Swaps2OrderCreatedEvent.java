package io.lastwill.eventscan.events.model;

import io.lastwill.eventscan.model.NetworkType;
import io.lastwill.eventscan.model.Swaps2Order;
import io.mywish.blockchain.WrapperTransaction;
import lombok.Getter;

@Getter
public class Swaps2OrderCreatedEvent extends BaseEvent {
    private final Swaps2Order order;
    private final WrapperTransaction transaction;

    public Swaps2OrderCreatedEvent(NetworkType networkType, Swaps2Order order, WrapperTransaction transaction) {
        super(networkType);
        this.order = order;
        this.transaction = transaction;
    }
}
