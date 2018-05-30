package io.lastwill.eventscan.services;

import io.lastwill.eventscan.model.NetworkType;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import java.math.BigInteger;
import java.util.concurrent.CompletableFuture;

@Slf4j
@Component
public class BalanceProvider {
    @Autowired
    private NetworkProvider networkProvider;

    public CompletableFuture<BigInteger> getBalanceAsync(NetworkType networkType, String address, long blockNo) {
        return networkProvider.get(networkType).getBalanceAsync(address, blockNo);
    }

    public BigInteger getBalance(NetworkType networkType, String address, long blockNo) throws Exception {
        return networkProvider.get(networkType).getBalance(address, blockNo);
    }
}
