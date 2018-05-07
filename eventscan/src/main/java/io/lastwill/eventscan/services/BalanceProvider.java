package io.lastwill.eventscan.services;

import io.mywish.scanner.model.NetworkType;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.DefaultBlockParameterNumber;
import org.web3j.protocol.core.methods.response.EthGetBalance;
import java.io.IOException;
import java.math.BigInteger;
import java.util.concurrent.CompletableFuture;

@Slf4j
@Component
public class BalanceProvider {
    @Autowired
    private Web3Provider web3Provider;

    public CompletableFuture<BigInteger> getBalanceAsync(NetworkType networkType, String address, long blockNo) {
        Web3j web3j = web3Provider.get(networkType);
        return web3j.ethGetBalance(address, new DefaultBlockParameterNumber(blockNo))
                .sendAsync()
                .thenApply(EthGetBalance::getBalance);
    }

    public BigInteger getBalance(NetworkType networkType, String address, long blockNo) throws IOException {
        return web3Provider.get(networkType)
                .ethGetBalance(address, new DefaultBlockParameterNumber(blockNo))
                .send()
                .getBalance();
    }
}
