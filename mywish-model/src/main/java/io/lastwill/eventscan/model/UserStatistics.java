package io.lastwill.eventscan.model;

import lombok.Getter;

@Getter
public class UserStatistics {
    private final long userCount;
    private final boolean isRegistered;

    public UserStatistics(long userCount, boolean isRegistered) {
        this.userCount = userCount;
        this.isRegistered = isRegistered;
    }
}
