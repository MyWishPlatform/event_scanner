package io.lastwill.eventscan.services.monitors;

import io.lastwill.eventscan.events.TransactionUnlockedEvent;
import io.lastwill.eventscan.repositories.AddressLockRepository;
import io.lastwill.eventscan.services.TransactionProvider;
import io.mywish.scanner.services.EventPublisher;
import io.mywish.wrapper.WrapperTransactionReceipt;
import io.mywish.scanner.model.NewBlockEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

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
    private void onNewBlock(final NewBlockEvent event) {
        Set<String> addresses = event.getTransactionsByAddress()
                .entrySet()
                .stream()
                .filter(entry -> entry.getValue().stream().anyMatch(tx -> entry.getKey().equalsIgnoreCase(tx.getInputs().size() > 0 ? tx.getInputs().get(0) : null)))
                .map(Map.Entry::getKey)
                .map(String::toLowerCase)
                .collect(Collectors.toSet());

        if (addresses.isEmpty()) {
            return;
        }

        addressLockRepository.findByAddressesList(event.getNetworkType(), addresses)
                .forEach(addressLock -> {
                    event.getTransactionsByAddress()
                            .get(addressLock.getAddress().toLowerCase())
                            .stream()
                            .filter(tx -> addressLock.getAddress().equalsIgnoreCase(tx.getInputs().get(0)))
                            .forEach(tx -> {
                                WrapperTransactionReceipt receipt;
                                try {
                                    receipt = transactionProvider.getTransactionReceipt(event.getNetworkType(), tx);
                                }
                                catch (IOException e) {
                                    log.warn("Getting transaction receipt failed.", e);
                                    return;
                                }

                                eventPublisher.publish(new TransactionUnlockedEvent(event.getNetworkType(), addressLock, tx, receipt));
                            });
                });
    }
}