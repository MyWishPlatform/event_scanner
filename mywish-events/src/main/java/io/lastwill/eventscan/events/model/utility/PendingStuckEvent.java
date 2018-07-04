package io.lastwill.eventscan.events.model.utility;

import io.lastwill.eventscan.events.model.BaseEvent;
import io.lastwill.eventscan.model.NetworkType;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class PendingStuckEvent extends BaseEvent {
    private final LocalDateTime receivedTime;
    private final int count;
    public PendingStuckEvent(NetworkType networkType, LocalDateTime receivedTime, int count) {
        super(networkType);
        this.receivedTime = receivedTime;
        this.count = count;
    }
}
