package io.lastwill.eventscan.events;

import io.lastwill.eventscan.model.CryptoCurrency;
import io.mywish.scanner.BaseEvent;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.web3j.protocol.core.methods.response.EthBlock;
import org.web3j.protocol.core.methods.response.Transaction;

import java.math.BigInteger;

@Getter
@RequiredArgsConstructor
public abstract class PaymentEvent extends BaseEvent {
    private final Transaction transaction;
    private final String address;
    private final BigInteger amount;
    private final CryptoCurrency currency;
    private final boolean isSuccess;
}
