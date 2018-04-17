package io.lastwill.eventscan.events.utility;

import io.mywish.scanner.model.BaseEvent;
import io.mywish.scanner.model.NetworkType;
import lombok.Getter;

import java.time.Instant;

@Getter
public class NetworkStuckEvent extends BaseEvent {
    private final Instant lastTimestamp;
    private final long lastBlockNo;
    public NetworkStuckEvent(NetworkType networkType, Instant lastTimestamp, long lastBlockNo) {
        super(networkType);
        this.lastTimestamp = lastTimestamp;
        this.lastBlockNo = lastBlockNo;
    }
}
