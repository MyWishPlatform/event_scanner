package io.mywish.scanner;

import io.mywish.scanner.model.NetworkType;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.math.BigInteger;
import java.util.concurrent.CompletableFuture;

public abstract class Network {
    private final NetworkType type;

    public Network(NetworkType type) {
        this.type = type;
    }

    public NetworkType getType() {
        return type;
    }

    abstract public Long getLastBlock() throws IOException;
    abstract public BigInteger getBalance(String address, Long blockNo) throws IOException;
    abstract public WrapperTransactionReceipt getTxReceipt(String hash) throws IOException;

    public CompletableFuture<BigInteger> getBalanceAsync(String address, Long blockNo) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                return getBalance(address, blockNo);
            } catch (java.io.IOException e) {
                throw new UncheckedIOException(e);
            }
        });
    }

    public CompletableFuture<WrapperTransactionReceipt> getTxReceiptAsync(String hash) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                return getTxReceipt(hash);
            } catch (java.io.IOException e) {
                throw new UncheckedIOException(e);
            }
        });
    }
}
