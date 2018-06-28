package io.mywish.scanner.services;

import io.mywish.wrapper.WrapperTransaction;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.TreeMap;

@Slf4j
@Component
public class PendingTransactionService {
    @Value("${etherscanner.pending-transactions-trashhold}")
    private int transactionsTrashhold;
    private final TreeMap<PendingKey, WrapperTransaction> pendingTransactions = new TreeMap<>();

    public void updatePending(List<WrapperTransaction> transactions, LocalDateTime now) {
        if (transactions.isEmpty()) {
            return;
        }
        transactions.forEach(wrapperTransaction -> pendingTransactions.put(
                new PendingKey(
                        wrapperTransaction.getHash(), now
                ),
                wrapperTransaction
        ));

        log.info("Updated {} transaction, now list has {} transactions.", transactions.size(), pendingTransactions.size());
        while (pendingTransactions.size() > transactionsTrashhold) {
            PendingKey lastKey = pendingTransactions.lastKey();
            log.debug("Removing pending transaction at {}.", lastKey.createdTime);
            pendingTransactions.remove(lastKey);
        }
        if (!pendingTransactions.isEmpty()) {
            log.info("Last pending transaction is at {}.", pendingTransactions.lastKey().createdTime);
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
