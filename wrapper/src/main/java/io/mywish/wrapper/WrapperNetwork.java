package io.mywish.wrapper;

import io.lastwill.eventscan.model.NetworkType;

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

    abstract public Long getLastBlock() throws Exception;
    abstract public BigInteger getBalance(String address, Long blockNo) throws Exception;
    abstract public WrapperBlock getBlock(String hash) throws Exception;
    abstract public WrapperBlock getBlock(Long number) throws Exception;
    abstract public WrapperTransaction getTransaction(String hash) throws Exception;
    abstract public WrapperTransactionReceipt getTxReceipt(WrapperTransaction transaction) throws Exception;

    public CompletableFuture<BigInteger> getBalanceAsync(String address, Long blockNo) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                return getBalance(address, blockNo);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        });
    }

    public CompletableFuture<WrapperTransactionReceipt> getTxReceiptAsync(WrapperTransaction transaction) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                return getTxReceipt(transaction);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        });
    }
}
