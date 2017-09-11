package io.lastwill.eventscan.messages;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

@ToString
@Getter
@RequiredArgsConstructor
public abstract class NotifyContract {
    private final int contractId;
    private final PaymentStatus state;

    public abstract String getType();
}
