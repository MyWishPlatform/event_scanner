package io.lastwill.eventscan.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.DefaultBlockParameterName;
import org.web3j.protocol.core.DefaultBlockParameterNumber;
import org.web3j.protocol.core.methods.response.EthGetBalance;

import java.io.IOException;
import java.math.BigInteger;
import java.util.concurrent.CompletableFuture;

@Component
public class BalanceProvider {
    @Autowired
    private Web3j web3j;

    public BigInteger getBalance(String address) throws IOException {
        return web3j.ethGetBalance(address, DefaultBlockParameterName.LATEST)
                .send()
                .getBalance();
    }

    public CompletableFuture<BigInteger> getBalanceAsync(String address) {
        return web3j.ethGetBalance(address, DefaultBlockParameterName.LATEST)
                .sendAsync()
                .thenApply(EthGetBalance::getBalance);
    }

    public CompletableFuture<BigInteger> getBalanceAsync(String address, long blockNo) {
        return web3j.ethGetBalance(address, new DefaultBlockParameterNumber(blockNo))
                .sendAsync()
                .thenApply(EthGetBalance::getBalance);
    }
}
