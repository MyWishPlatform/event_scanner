package io.lastwill.eventscan.services.monitors;

import io.lastwill.eventscan.events.ContractCreatedEvent;
import io.lastwill.eventscan.events.ContractEventsEvent;
import io.lastwill.eventscan.events.contract.ContractEvent;
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
import java.util.HashMap;
import java.util.List;
import java.util.Set;

@Slf4j
@Component
public class ContractsMonitor {
    @Autowired
    private ContractRepository contractRepository;

    @Autowired
    private EventPublisher eventPublisher;

    @Autowired
    private TransactionProvider transactionProvider;

    @Autowired
    private EventParser eventParser;

    @Value("${io.lastwill.eventscan.contract.proxy-address}")
    private String proxyAddress;

    @PostConstruct
    protected void init() {
        if (proxyAddress != null) {
            proxyAddress = proxyAddress.toLowerCase();
        }
    }

    @EventListener
    public void onNewBlock(final NewBlockEvent event) {
        Set<String> addresses = event.getTransactionsByAddress().keySet();
        if (addresses.isEmpty()) {
            return;
        }

        if (addresses.contains(proxyAddress)) {
            final List<Transaction> transactions = event.getTransactionsByAddress().get(proxyAddress);
            grabProxyEvents(transactions, event.getBlock());
        }

        List<Contract> contracts = contractRepository.findByAddressesList(addresses);
        for (final Contract contract : contracts) {
            if (contract.getAddress() == null || !addresses.contains(contract.getAddress().toLowerCase())) {
                continue;
            }

            final List<Transaction> transactions = event.getTransactionsByAddress().get(contract.getAddress().toLowerCase());
            for (final Transaction transaction : transactions) {
                // grab events
                if (transaction.getTo() == null) {
                    continue;
                }

                grabContractEvents(contract, transaction, event.getBlock());
            }
        }
    }

    private void grabProxyEvents(final List<Transaction> transactions, final EthBlock.Block block) {
        for (Transaction transaction : transactions) {
            transactionProvider.getTransactionReceiptAsync(transaction.getHash())
                    .thenAccept(transactionReceipt -> {
                        MultiValueMap<String, Log> logsByAddress = CollectionUtils.toMultiValueMap(new HashMap<>());
                        for (Log log : transactionReceipt.getLogs()) {
                            logsByAddress.add(log.getAddress(), log);
                        }

                        contractRepository.findByAddressesList(logsByAddress.keySet());
                        for (Contract contract : contractRepository.findByAddressesList(logsByAddress.keySet())) {
                            List<ContractEvent> eventValues;
                            try {
                                eventValues = eventParser.parseEvents(transactionReceipt);
                            }
                            catch (Throwable e) {
                                log.error("Error on parsing events.", e);
                                return;
                            }
                            if (eventValues.isEmpty()) {
                                return;
                            }
                            eventPublisher.publish(
                                    new ContractEventsEvent(
                                            contract,
                                            eventValues,
                                            transaction,
                                            transactionReceipt,
                                            block
                                    )
                            );
                        }

                    });
        }
    }

    private void grabContractEvents(final Contract contract, final Transaction transaction, final EthBlock.Block block) {
        transactionProvider.getTransactionReceiptAsync(transaction.getHash())
                .thenAccept(transactionReceipt -> {
                    List<ContractEvent> eventValues;
                    try {
                        eventValues = eventParser.parseEvents(transactionReceipt);
                    }
                    catch (Throwable e) {
                        log.error("Error on parsing events.", e);
                        return;
                    }
                    if (eventValues.isEmpty()) {
                        return;
                    }
                    eventPublisher.publish(
                            new ContractEventsEvent(
                                    contract,
                                    eventValues,
                                    transaction,
                                    transactionReceipt,
                                    block
                            )
                    );
                });
    }
}
