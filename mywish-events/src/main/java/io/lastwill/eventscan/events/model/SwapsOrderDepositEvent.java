package io.lastwill.eventscan.events.model;

import io.lastwill.eventscan.events.model.contract.swaps2.DepositEvent;
import io.lastwill.eventscan.model.CryptoCurrency;
import io.lastwill.eventscan.model.NetworkType;
import io.lastwill.eventscan.model.Swaps2Order;
import io.mywish.blockchain.WrapperTransaction;
import lombok.Getter;

import java.math.BigInteger;

@Getter
public class SwapsOrderDepositEvent extends BaseEvent {

    private final String token;
    private final String user;
    private final BigInteger amount;
    private final BigInteger balance;
    private final Swaps2Order order;
    private final WrapperTransaction transaction;
    CryptoCurrency currency;

    public SwapsOrderDepositEvent(NetworkType networkType,
                                  Swaps2Order order,
                                  WrapperTransaction transaction,
                                  DepositEvent event,
                                  CryptoCurrency cryptoCurrency
    ) {
        super(networkType);
        this.order = order;
        this.transaction = transaction;
        this.token = event.getToken();
        this.user = event.getUser();
        this.amount = event.getAmount();
        this.balance = event.getBalance();
        this.currency = cryptoCurrency;
    }
}
