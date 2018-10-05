package io.lastwill.eventscan.services.monitors;

import io.lastwill.eventscan.events.model.ContractCreatedEvent;
import io.lastwill.eventscan.model.Contract;
import io.lastwill.eventscan.repositories.ContractRepository;
import io.lastwill.eventscan.services.NetworkProvider;
import io.lastwill.eventscan.services.TransactionProvider;
import io.mywish.scanner.services.EventPublisher;
import io.mywish.blockchain.WrapperNetwork;
import io.mywish.blockchain.WrapperTransaction;
import io.mywish.scanner.model.NewBlockEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

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

    @Autowired
    private NetworkProvider networkProvider;

    @EventListener
    private void onNewBlock(final NewBlockEvent event) {
        WrapperNetwork network = networkProvider.get(event.getNetworkType());
        Set<String> addresses = event.getTransactionsByAddress().keySet();
        if (addresses.isEmpty()) {
            return;
        }

        Map<String, WrapperTransaction> deployHashes = event.getTransactionsByAddress()
                .values()
                .stream()
                .flatMap(Collection::stream)
                .filter(WrapperTransaction::isContractCreation)
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
            final WrapperTransaction transaction = deployHashes.get(contract.getTxHash().toLowerCase());
            transactionProvider.getTransactionReceiptAsync(event.getNetworkType(), transaction)
                    .thenAccept(transactionReceipt -> {
                        if (!transactionReceipt.isSuccess()) {
                            log.warn("Failed contract ({}) creation in transaction {}!", contract.getId(), transaction.getHash());
                        }
                        else {
                            contract.setAddress(transactionReceipt.getContracts().get(0).toLowerCase());
                            if (transaction.getCreates() == null) {
                                transaction.setCreates(transactionReceipt.getContracts().get(0));
                            }
                        }
                        eventPublisher.publish(new ContractCreatedEvent(
                                event.getNetworkType(),
                                contract,
                                transaction,
                                event.getBlock(),
                                transaction.getCreates(),
                                transactionReceipt.isSuccess())
                        );
                    })
                    .exceptionally(throwable -> {
                        log.warn("ContractCreatedEvent handling failed.", throwable);
                        return null;
                    });
        }
    }
}
