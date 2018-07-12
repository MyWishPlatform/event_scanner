package io.lastwill.eventscan.messages;


import lombok.Getter;

import java.math.BigInteger;

@Getter
public class ExFundsAddedNotify extends FundsAddedNotify {
    private final String investorAddress;

    public ExFundsAddedNotify(int contractId, String transactionHash, BigInteger value, BigInteger balance, String investorAddress) {
        super(contractId, transactionHash, value, balance);
        this.investorAddress = investorAddress;
    }
}
