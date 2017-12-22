package io.lastwill.eventscan.messages;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

@ToString
@Getter
@RequiredArgsConstructor
public abstract class BaseNotify {
    private final PaymentStatus status;
    private final String transactionHash;

    public abstract String getType();
}
