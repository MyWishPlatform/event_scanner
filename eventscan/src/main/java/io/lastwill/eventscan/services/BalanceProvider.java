package io.lastwill.eventscan.services;

import io.mywish.scanner.model.NetworkType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.DefaultBlockParameterName;
import org.web3j.protocol.core.DefaultBlockParameterNumber;
import org.web3j.protocol.core.methods.response.EthGetBalance;

import java.io.IOException;
import java.math.BigInteger;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

@Component
public class BalanceProvider {
    @Autowired
    private Web3Provider web3Provider;

    public CompletableFuture<BigInteger> getBalanceAsync(NetworkType networkType, String address, long blockNo) {
        Web3j web3j = web3Provider.get(networkType);
        if (web3j == null) {
            throw new UnsupportedOperationException(networkType + " network is not supported for getting balance.");
        }
        return web3j.ethGetBalance(address, new DefaultBlockParameterNumber(blockNo))
                .sendAsync()
                .thenApply(EthGetBalance::getBalance);
    }
}
