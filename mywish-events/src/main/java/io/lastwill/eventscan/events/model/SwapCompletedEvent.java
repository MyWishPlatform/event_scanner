package io.lastwill.eventscan.events.model;

import io.lastwill.eventscan.model.NetworkType;
import io.mywish.blockchain.WrapperTransaction;
import lombok.Getter;

@Getter
public class SwapCompletedEvent extends BaseEvent {
    private final WrapperTransaction transaction;
    private final Integer id;
    private Integer productId;

    public SwapCompletedEvent(
            NetworkType networkType,
            WrapperTransaction transaction,
            Integer id,
            Integer productId
    ) {
        super(networkType);
        this.transaction = transaction;
        this.id = id;
        this.productId = productId;
    }
}