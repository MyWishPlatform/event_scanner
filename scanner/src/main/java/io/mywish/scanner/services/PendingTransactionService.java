package io.mywish.scanner.services;

import io.lastwill.eventscan.events.model.PendingTransactionAddedEvent;
import io.lastwill.eventscan.events.model.PendingTransactionRemovedEvent;
import io.lastwill.eventscan.model.NetworkType;
import io.mywish.scanner.model.NewBlockEvent;
import io.mywish.scanner.model.NewPendingTransactionsEvent;
import io.mywish.blockchain.WrapperBlock;
import io.mywish.blockchain.WrapperTransaction;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.event.EventListener;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.TreeMap;

@Slf4j
public class PendingTransactionService {
    @Autowired
    private EventPublisher eventPublisher;
    @Value("${etherscanner.pending-transactions-threshold:0}")
    private int transactionsThreshold;

    private final NetworkType networkType;
    private final TreeMap<LocalDateTime, List<String>> transactionsByTime = new TreeMap<>();
    private final HashMap<String, WrapperTransaction> transactionsByHash = new HashMap<>();

    public PendingTransactionService(NetworkType networkType) {
        this.networkType = networkType;
    }

    @EventListener
    public void updatePending(NewPendingTransactionsEvent event) {
        if (networkType != event.getNetworkType()) {
            return;
        }

        LocalDateTime now = LocalDateTime.now(ZoneOffset.UTC);
        List<WrapperTransaction> transactions = event.getPendingTransactions();
        if (transactions.isEmpty()) {
            return;
        }
        List<String> hashes = transactionsByTime.getOrDefault(now, new ArrayList<>());
        for (WrapperTransaction wrapperTransaction : transactions) {
            if (transactionsByHash.containsKey(wrapperTransaction.getHash())) {
                continue;
            }
            transactionsByHash.put(wrapperTransaction.getHash(), wrapperTransaction);
            hashes.add(wrapperTransaction.getHash());
            try {
                eventPublisher.publish(new PendingTransactionAddedEvent(
                        networkType,
                        wrapperTransaction
                ));
            }
            catch (Exception e) {
                log.warn("{}: Exception occurs on handling new pending transaction.", networkType, e);
            }
        }
        transactionsByTime.put(now, hashes);

        log.info("{}: {} new transactions, now list has {} transactions, threshold is {}.",
                networkType,
                transactions.size(),
                transactionsByHash.size(),
                transactionsThreshold
        );
        while (transactionsByHash.size() > transactionsThreshold) {
            LocalDateTime firstKey = transactionsByTime.firstKey();
            log.debug("{}: removing pending transaction at {}.", networkType, firstKey);
            List<String> oldHashes = transactionsByTime.remove(firstKey);
            for (String hash : oldHashes) {
                WrapperTransaction transaction = transactionsByHash.remove(hash);
                if (transaction == null) {
                    continue;
                }
                try {
                    eventPublisher.publish(new PendingTransactionRemovedEvent(
                            networkType,
                            transaction,
                            PendingTransactionRemovedEvent.Reason.TIMEOUT,
                            null
                    ));
                }
                catch (Exception e) {
                    log.warn("{}: Exception occurs on removing outdated transaction.", networkType, e);
                }
            }
        }
        if (!transactionsByHash.isEmpty()) {
            log.info("{}: most early pending transaction is at {}.", networkType, transactionsByTime.firstKey());
        }
    }


    @EventListener
    public void newBlock(NewBlockEvent event) {
        if (networkType != event.getNetworkType()) {
            return;
        }

        WrapperBlock block = event.getBlock();
        int counter = 0;
        for (WrapperTransaction transaction : block.getTransactions()) {
            WrapperTransaction removedTransaction = transactionsByHash.remove(transaction.getHash());
            if (removedTransaction == null) {
                continue;
            }
            counter++;
            eventPublisher.publish(new PendingTransactionRemovedEvent(
                    networkType,
                    removedTransaction,
                    PendingTransactionRemovedEvent.Reason.ACCEPTED,
                    block.getNumber()
            ));
        }
        if (counter > 0) {
            log.info("{}: remove transactions {} because of block {}.", networkType, counter, block.getNumber());
        }
    }
}
