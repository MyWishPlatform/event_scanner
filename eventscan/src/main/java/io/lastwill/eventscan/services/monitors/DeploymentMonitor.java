package io.lastwill.eventscan.services.monitors;

import io.lastwill.eventscan.events.ContractCreatedEvent;
import io.lastwill.eventscan.helpers.TransactionHelper;
import io.lastwill.eventscan.model.Contract;
import io.lastwill.eventscan.repositories.ContractRepository;
import io.lastwill.eventscan.services.TransactionProvider;
import io.mywish.scanner.services.EventPublisher;
import io.mywish.scanner.model.NewBlockEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import org.web3j.protocol.core.methods.response.Transaction;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

@Slf4j
@Component
public class DeploymentMonitor {
    @Autowired
    private ContractRepository contractRepository;

    @Autowired
    private EventPublisher eventPublisher;

    @Autowired
    private TransactionProvider transactionProvider;

    @EventListener
    public void onNewBlock(final NewBlockEvent event) {
        Set<String> addresses = event.getTransactionsByAddress().keySet();
        if (addresses.isEmpty()) {
            return;
        }

        Map<String, Transaction> deployHashes = event.getTransactionsByAddress()
                .values()
                .stream()
                .flatMap(Collection::stream)
                .filter(tr -> tr.getTo() == null)
                .collect(Collectors.toMap(
                        tr -> tr.getHash().toLowerCase(),
                        Function.identity(),
                        (hash1, hash2) -> hash1
                ));

        if (deployHashes.isEmpty()) {
            return;
        }

        List<Contract> contracts = contractRepository.findByTxHashes(deployHashes.keySet(), event.getNetworkType());
        for (Contract contract : contracts) {
            final Transaction transaction = deployHashes.get(contract.getTxHash().toLowerCase());
            transactionProvider.getTransactionReceiptAsync(event.getNetworkType(), contract.getTxHash())
                    .thenAccept(transactionReceipt -> {
                        if (!TransactionHelper.isSuccess(transactionReceipt)) {
                            log.warn("Failed contract ({}) creation in transaction {}!", contract.getId(), transaction.getHash());
                        }
                        else {
                            contract.setAddress(transactionReceipt.getContractAddress().toLowerCase());
                            if (transaction.getCreates() == null) {
                                transaction.setCreates(transactionReceipt.getContractAddress());
                            }
                        }
                        eventPublisher.publish(new ContractCreatedEvent(
                                event.getNetworkType(),
                                contract,
                                transaction,
                                event.getBlock(), TransactionHelper.isSuccess(transactionReceipt))
                        );
                    })
                    .exceptionally(throwable -> {
                        log.warn("ContractCreatedEvent handling failed.", throwable);
                        return null;
                    });
        }
    }
}
