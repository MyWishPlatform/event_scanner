package io.mywish.scanner.services;

import io.lastwill.eventscan.events.model.PendingTransactionAddedEvent;
import io.lastwill.eventscan.events.model.PendingTransactionRemovedEvent;
import io.lastwill.eventscan.model.NetworkType;
import io.mywish.wrapper.WrapperBlock;
import io.mywish.wrapper.WrapperTransaction;
import lombok.extern.slf4j.Slf4j;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.List;
import java.util.Objects;
import java.util.TreeMap;

@Slf4j
public class PendingTransactionService {
    private final EventPublisher eventPublisher;
    private final NetworkType networkType;
    private final TreeMap<PendingKey, WrapperTransaction> pendingTransactions = new TreeMap<>();
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
        for (WrapperTransaction wrapperTransaction : transactions) {
            PendingKey key = new PendingKey(wrapperTransaction.getHash(), now);
            if (pendingTransactions.containsKey(key)) {
                continue;
            }
            pendingTransactions.put(key, wrapperTransaction);
            eventPublisher.publish(new PendingTransactionAddedEvent(
                    networkType,
                    wrapperTransaction
            ));
        }

        log.info("Updated {} transaction, now list has {} transactions, threshold {}.", transactions.size(), pendingTransactions.size(), transactionsThreshold);
        while (pendingTransactions.size() > transactionsThreshold) {
            PendingKey lastKey = pendingTransactions.lastKey();
            log.debug("Removing pending transaction at {}.", lastKey.createdTime);
            WrapperTransaction transaction = pendingTransactions.remove(lastKey);
            eventPublisher.publish(new PendingTransactionRemovedEvent(
                    networkType,
                    transaction,
                    PendingTransactionRemovedEvent.Reason.TIMEOUT,
                    null
            ));
        }
        if (!pendingTransactions.isEmpty()) {
            log.info("Last pending transaction is at {}.", pendingTransactions.lastKey().createdTime);
        }
    }

    public void newBlock(WrapperBlock block) {
        LocalDateTime now = LocalDateTime.ofEpochSecond(block.getTimestamp(), 0, ZoneOffset.UTC);
        int counter = 0;
        for (WrapperTransaction transaction : block.getTransactions()) {
            PendingKey key = new PendingKey(transaction.getHash(), now);
            WrapperTransaction removedTransaction = pendingTransactions.remove(key);
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
            log.info("Remove transactions {} because of block {}.", counter, block.getNumber());
        }
    }

    public static class PendingKey implements Comparable<PendingKey> {
        private final String transactionHash;
        private final LocalDateTime createdTime;

        public PendingKey(String transactionHash, LocalDateTime createdTime) {
            this.transactionHash = transactionHash;
            this.createdTime = createdTime;
        }

        @Override
        public int compareTo(PendingKey o) {
            return createdTime.compareTo(o.createdTime);
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (!(o instanceof PendingKey)) return false;
            PendingKey that = (PendingKey) o;
            return Objects.equals(transactionHash, that.transactionHash);
        }

        @Override
        public int hashCode() {
            return Objects.hash(transactionHash);
        }
    }
}
