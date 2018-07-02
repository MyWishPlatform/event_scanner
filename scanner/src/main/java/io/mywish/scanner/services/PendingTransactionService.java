package io.mywish.scanner.services;

import io.lastwill.eventscan.events.model.PendingTransactionAddedEvent;
import io.lastwill.eventscan.events.model.PendingTransactionRemovedEvent;
import io.lastwill.eventscan.model.NetworkType;
import io.mywish.wrapper.WrapperBlock;
import io.mywish.wrapper.WrapperTransaction;
import lombok.extern.slf4j.Slf4j;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.TreeMap;

@Slf4j
public class PendingTransactionService {
    private final EventPublisher eventPublisher;
    private final NetworkType networkType;
    private final TreeMap<LocalDateTime, List<String>> transactionsByTime = new TreeMap<>();
    private final HashMap<String, WrapperTransaction> transactionsByHash = new HashMap<>();
    private final int transactionsThreshold;

    public PendingTransactionService(EventPublisher eventPublisher, NetworkType networkType, int transactionsThreshold) {
        this.eventPublisher = eventPublisher;
        this.networkType = networkType;
        this.transactionsThreshold = transactionsThreshold;
    }

    public void updatePending(List<WrapperTransaction> transactions, LocalDateTime now) {
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

    public void newBlock(WrapperBlock block) {
        int counter = 0;
        for (WrapperTransaction transaction : block.getTransactions()) {
            WrapperTransaction removedTransaction = transactionsByHash.remove(transaction.getHash());
            if (removedTransaction == null) {
                continue;
            }
            counter ++;
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
