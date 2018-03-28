package io.lastwill.eventscan.services.monitors;

import io.lastwill.eventscan.events.TransactionUnlockedEvent;
import io.lastwill.eventscan.repositories.AddressLockRepository;
import io.lastwill.eventscan.services.TransactionProvider;
import io.mywish.scanner.services.EventPublisher;
import io.mywish.scanner.model.NewBlockEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import org.web3j.protocol.core.methods.response.TransactionReceipt;

import java.io.IOException;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@Component
public class LockMonitor {
    @Autowired
    private AddressLockRepository addressLockRepository;

    @Autowired
    private TransactionProvider transactionProvider;

    @Autowired
    private EventPublisher eventPublisher;

    @EventListener
    public void onNewBlock(final NewBlockEvent event) {
        Set<String> addresses = event.getTransactionsByAddress()
                .entrySet()
                .stream()
                .filter(entry -> entry.getValue().stream().anyMatch(tx -> entry.getKey().equalsIgnoreCase(tx.getFrom())))
                .map(Map.Entry::getKey)
                .map(String::toLowerCase)
                .collect(Collectors.toSet());

        if (addresses.isEmpty()) {
            return;
        }

        addressLockRepository.findByAddressesList(addresses)
                .forEach(addressLock -> {
                    event.getTransactionsByAddress()
                            .get(addressLock.getAddress().toLowerCase())
                            .stream()
                            .filter(tx -> addressLock.getAddress().equalsIgnoreCase(tx.getFrom()))
                            .forEach(tx -> {
                                TransactionReceipt receipt;
                                try {
                                    receipt = transactionProvider.getTransactionReceipt(tx.getHash());
                                }
                                catch (IOException e) {
                                    log.warn("Getting transaction receipt failed.", e);
                                    return;
                                }

                                eventPublisher.publish(new TransactionUnlockedEvent(addressLock, tx, receipt));
                            });
                });
    }
}