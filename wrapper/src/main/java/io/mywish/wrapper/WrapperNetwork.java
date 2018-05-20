package io.mywish.wrapper;

import io.lastwill.eventscan.model.NetworkType;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.math.BigInteger;
import java.util.concurrent.CompletableFuture;

public abstract class WrapperNetwork {
    private final NetworkType type;

    public WrapperNetwork(NetworkType type) {
        this.type = type;
    }

    public NetworkType getType() {
        return type;
    }

    abstract public Long getLastBlock() throws IOException;
    abstract public BigInteger getBalance(String address, Long blockNo) throws IOException;
    abstract public WrapperBlock getBlock(String hash) throws java.io.IOException;
    abstract public WrapperBlock getBlock(Long number) throws java.io.IOException;
    abstract public WrapperTransaction getTransaction(String hash) throws IOException;
    abstract public WrapperTransactionReceipt getTxReceipt(WrapperTransaction transaction) throws IOException;

    public CompletableFuture<BigInteger> getBalanceAsync(String address, Long blockNo) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                return getBalance(address, blockNo);
            } catch (java.io.IOException e) {
                throw new UncheckedIOException(e);
            }
        });
    }

    public CompletableFuture<WrapperTransactionReceipt> getTxReceiptAsync(WrapperTransaction transaction) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                return getTxReceipt(transaction);
            } catch (java.io.IOException e) {
                throw new UncheckedIOException(e);
            }
        });
    }
}
