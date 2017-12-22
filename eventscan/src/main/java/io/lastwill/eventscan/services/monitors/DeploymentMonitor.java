package io.lastwill.eventscan.services.monitors;

import io.lastwill.eventscan.events.ContractCreatedEvent;
import io.lastwill.eventscan.events.ContractEventsEvent;
import io.lastwill.eventscan.helpers.TransactionHelper;
import io.lastwill.eventscan.model.Contract;
import io.lastwill.eventscan.model.EventValue;
import io.lastwill.eventscan.model.Product;
import io.lastwill.eventscan.repositories.ContractRepository;
import io.lastwill.eventscan.repositories.ProductRepository;
import io.lastwill.eventscan.services.EventParser;
import io.lastwill.eventscan.services.TransactionProvider;
import io.mywish.scanner.EventPublisher;
import io.mywish.scanner.NewBlockEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import org.springframework.util.MultiValueMap;
import org.web3j.protocol.core.methods.response.EthBlock;
import org.web3j.protocol.core.methods.response.Log;
import org.web3j.protocol.core.methods.response.Transaction;

import javax.annotation.PostConstruct;
import java.util.*;
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
                .collect(Collectors.toMap(tr -> tr.getHash().toLowerCase(), Function.identity()));

        if (deployHashes.isEmpty()) {
            return;
        }

        List<Contract> contracts = contractRepository.findByTxHashes(deployHashes.keySet());
        for (Contract contract: contracts) {
            final Transaction transaction = deployHashes.get(contract.getTxHash().toLowerCase());
            transactionProvider.getTransactionReceiptAsync(contract.getTxHash())
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
                                contract,
                                transaction,
                                event.getBlock(),
                                TransactionHelper.isSuccess(transactionReceipt))
                        );
                    });
        }
    }
}
