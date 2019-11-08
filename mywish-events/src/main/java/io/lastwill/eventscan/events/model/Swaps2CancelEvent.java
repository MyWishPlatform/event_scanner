package io.lastwill.eventscan.events.model;

import io.lastwill.eventscan.model.NetworkType;
import io.lastwill.eventscan.model.Swaps2Order;
import io.mywish.blockchain.WrapperTransaction;
import lombok.Getter;

@Getter
public class Swaps2CancelEvent extends BaseEvent {
    private final WrapperTransaction transaction;
    private final Swaps2Order order;

    public Swaps2CancelEvent(
            NetworkType networkType,
            Swaps2Order order,
            WrapperTransaction transaction
    ) {
        super(networkType);
        this.order = order;
        this.transaction = transaction;
    }
}
