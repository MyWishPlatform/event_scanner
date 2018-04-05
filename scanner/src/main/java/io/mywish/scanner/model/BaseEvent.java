package io.mywish.scanner.model;

import lombok.Getter;

@Getter
public class BaseEvent {
    private final NetworkType networkType;

    public BaseEvent(NetworkType networkType) {
        this.networkType = networkType;
    }
}
