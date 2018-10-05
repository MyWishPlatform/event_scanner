package io.lastwill.eventscan.services;

import io.mywish.blockchain.WrapperTransaction;
import io.mywish.blockchain.WrapperTransactionReceipt;
import io.lastwill.eventscan.model.NetworkType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
import java.util.stream.Collectors;

@Component
public class TransactionProvider {
    @Autowired
    private NetworkProvider networkProvider;

    public WrapperTransactionReceipt getTransactionReceipt(final NetworkType networkType, final WrapperTransaction transaction) throws Exception {
        return networkProvider.get(networkType).getTxReceipt(transaction);
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

    public CompletionStage<WrapperTransactionReceipt> getTransactionReceiptAsync(NetworkType networkType, WrapperTransaction transaction) {
        return networkProvider.get(networkType).getTxReceiptAsync(transaction);
    }
//
//    public CompletionStage<List<TransactionReceipt>> getTransactionReceiptsAsync(Collection<String> hashes) {
//        val futures = hashes.stream()
//                .map(web3Provider::ethGetTransactionReceipt)
//                .map(JsonRpcRequest::sendAsync)
//                .map(future -> future.thenApply(EthGetTransactionReceipt::getResult))
//                .collect(Collectors.toList());
//
//        return sequence(futures);
//    }
}
