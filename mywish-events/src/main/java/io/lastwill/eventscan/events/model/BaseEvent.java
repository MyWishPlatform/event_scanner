package io.lastwill.eventscan.events.model;

import io.lastwill.eventscan.model.NetworkType;
import lombok.Getter;

@Getter
public class BaseEvent {
    private final NetworkType networkType;

    public BaseEvent(NetworkType networkType) {
        this.networkType = networkType;
    }
}
