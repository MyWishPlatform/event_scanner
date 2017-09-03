package io.lastwill.eventscan.events;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.math.BigInteger;

@Getter
@RequiredArgsConstructor
public abstract class BalanceChangedEvent extends BaseEvent {
    private final String address;
    private final BigInteger amount;
    private final BigInteger balance;
}
