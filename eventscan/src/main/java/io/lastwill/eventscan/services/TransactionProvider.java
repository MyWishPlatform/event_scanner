package io.lastwill.eventscan.services;

import lombok.val;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.Request;
import org.web3j.protocol.core.methods.response.EthGetTransactionReceipt;
import org.web3j.protocol.core.methods.response.TransactionReceipt;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
import java.util.stream.Collectors;

@Component
public class TransactionProvider {
    @Autowired
    private Web3j web3j;

    public TransactionReceipt getTransactionReceipt(String hash) throws IOException {
        return web3j.ethGetTransactionReceipt(hash)
                .send()
                .getTransactionReceipt()
                .orElse(null);
    }

    private static <T> CompletableFuture<List<T>> sequence(List<CompletableFuture<T>> futures) {
        CompletableFuture<Void> allDoneFuture =
                CompletableFuture.allOf(futures.toArray(new CompletableFuture[futures.size()]));
        return allDoneFuture.thenApply(v ->
                futures.stream()
                        .map(CompletableFuture::join)
                        .filter(Objects::nonNull)
                        .collect(Collectors.toList())
        );
    }

    public CompletionStage<TransactionReceipt> getTransactionReceiptAsync(String hash) {
        return web3j.ethGetTransactionReceipt(hash)
                .sendAsync()
                .thenApply(EthGetTransactionReceipt::getResult);
    }

    public CompletionStage<List<TransactionReceipt>> getTransactionReceiptsAsync(Collection<String> hashes) {
        val futures = hashes.stream()
                .map(web3j::ethGetTransactionReceipt)
                .map(Request::sendAsync)
                .map(future -> future.thenApply(EthGetTransactionReceipt::getResult))
                .collect(Collectors.toList());

        return sequence(futures);
    }
}
