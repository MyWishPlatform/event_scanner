package io.lastwill.eventscan.events;

import io.mywish.scanner.BaseEvent;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.web3j.protocol.core.methods.response.EthBlock;

import java.math.BigInteger;

@Getter
@RequiredArgsConstructor
public abstract class BalanceChangedEvent extends BaseEvent {
    private final EthBlock.Block block;
    private final String address;
    private final BigInteger amount;
    private final BigInteger balance;
}
