package io.lastwill.eventscan.services;

import io.mywish.scanner.WrapperTransactionReceipt;
import io.mywish.scanner.model.NetworkType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
import java.util.stream.Collectors;

@Component
public class TransactionProvider {
    @Autowired
    private NetworkProvider networkProvider;

    public WrapperTransactionReceipt getTransactionReceipt(final NetworkType networkType, final String hash) throws IOException {
        return networkProvider.get(networkType).getTxReceipt(hash);
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

    public CompletionStage<WrapperTransactionReceipt> getTransactionReceiptAsync(NetworkType networkType, String hash) {
        return networkProvider.get(networkType).getTxReceiptAsync(hash);
    }
//
//    public CompletionStage<List<TransactionReceipt>> getTransactionReceiptsAsync(Collection<String> hashes) {
//        val futures = hashes.stream()
//                .map(web3Provider::ethGetTransactionReceipt)
//                .map(Request::sendAsync)
//                .map(future -> future.thenApply(EthGetTransactionReceipt::getResult))
//                .collect(Collectors.toList());
//
//        return sequence(futures);
//    }
}
