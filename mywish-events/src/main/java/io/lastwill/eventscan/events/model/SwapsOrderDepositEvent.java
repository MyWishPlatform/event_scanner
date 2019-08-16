package io.lastwill.eventscan.events.model;

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
                                  String token,
                                  String user,
                                  BigInteger amount,
                                  BigInteger balance,
                                  CryptoCurrency cryptoCurrency
    ) {
        super(networkType);
        this.order = order;
        this.transaction = transaction;
        this.token = token;
        this.user = user;
        this.amount = amount;
        this.balance = balance;
        this.currency = cryptoCurrency;
    }
}
