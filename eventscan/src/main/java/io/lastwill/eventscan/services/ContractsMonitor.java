package io.lastwill.eventscan.services;

import io.lastwill.eventscan.events.ContractCreatedEvent;
import io.lastwill.eventscan.events.ContractEventsEvent;
import io.lastwill.eventscan.events.NewBlockEvent;
import io.lastwill.eventscan.events.OwnerBalanceChangedEvent;
import io.lastwill.eventscan.model.Contract;
import io.lastwill.eventscan.model.EventValue;
import io.lastwill.eventscan.repositories.ContractRepository;
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

import java.math.BigInteger;
import java.util.ArrayList;
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
    private BalanceProvider balanceProvider;

    @Autowired
    private CommitmentService commitmentService;

    @Autowired
    private TransactionProvider transactionProvider;

    @Autowired
    private EventParser eventParser;

    @Value("${io.lastwill.eventscan.contract.proxy-address}")
    private String proxyAddress;

    @EventListener
    public void onNewBlock(NewBlockEvent newBlockEvent) {
        Set<String> addresses = newBlockEvent.getTransactionsByAddress().keySet();
        if (addresses.isEmpty()) {
            return;
        }
        if (log.isTraceEnabled()) {
            log.trace("List of address ({}):", addresses.size());
            addresses.forEach(log::trace);
        }

        if (addresses.contains(proxyAddress)) {
            final List<Transaction> transactions = newBlockEvent.getTransactionsByAddress().get(proxyAddress);
            grabProxyEvents(transactions, newBlockEvent.getBlock());
        }
        List<Contract> contracts = contractRepository.findByAddressesList(addresses);
        for (Contract contract : contracts) {
            boolean wasPublished = false;
            if (contract.getAddress() != null && addresses.contains(contract.getAddress().toLowerCase())) {
                final List<Transaction> transactions = newBlockEvent.getTransactionsByAddress().get(contract.getAddress().toLowerCase());
                grabContractEvents(contract, transactions, newBlockEvent.getBlock());
                wasPublished |= true;
            }
            if (addresses.contains(contract.getOwnerAddress().toLowerCase())) {
                final List<Transaction> transactions = newBlockEvent.getTransactionsByAddress().get(
                        contract.getOwnerAddress().toLowerCase()
                );

                final List<Transaction> input = new ArrayList<>(transactions.size());
                for (Transaction transaction: transactions) {
                    // get input transactions
                    if (contract.getOwnerAddress().equalsIgnoreCase(transaction.getTo())) {
                        input.add(transaction);
                    }
                    // output transactions
                    else if (contract.getOwnerAddress().equalsIgnoreCase(transaction.getFrom())) {
                        // contract creation
                        if (transaction.getTo() == null) {
                            eventPublisher.publish(new ContractCreatedEvent(contract, transaction, newBlockEvent.getBlock()));
                            wasPublished |= true;
                        }
                    }
                }
                if (!input.isEmpty()) {
                    publishOwnerBalance(
                            contract,
                            input,
                            newBlockEvent.getBlock()
                    );
                    wasPublished |= true;
                }
            }

            if (!wasPublished) {
                log.warn("Contract {} was mentioned in block because of unknown reason.", contract.getId());
            }
        }
    }

    private void grabProxyEvents(final List<Transaction> transactions, final EthBlock.Block block) {
        commitmentService.waitCommitment(block.getHash(), block.getNumber().longValue())
                .thenAccept(committed -> {
                    if (!committed) {
                        log.info("Block {} with contract's transactions was rejected.", block.getNumber());
                        return;
                    }

                    for (Transaction transaction : transactions) {
                        transactionProvider.getTransactionReceiptAsync(transaction.getHash())
                                .thenAccept(transactionReceipt -> {
                                    MultiValueMap<String, Log> logsByAddress = CollectionUtils.toMultiValueMap(new HashMap<>());
                                    for (Log log: transactionReceipt.getLogs()) {
                                        logsByAddress.add(log.getAddress(), log);
                                    }

                                    contractRepository.findByAddressesList(logsByAddress.keySet());
                                    for (Contract contract : contractRepository.findByAddressesList(logsByAddress.keySet())) {
                                        List<EventValue> eventValues;
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
                });
    }

    private void grabContractEvents(final Contract contract, final List<Transaction> transactions, final EthBlock.Block block) {
        commitmentService.waitCommitment(block.getHash(), block.getNumber().longValue())
                .thenAccept(committed -> {
                    if (!committed) {
                        log.info("Block {} with contract's transactions was rejected.", block.getNumber());
                        return;
                    }

                    for (Transaction transaction : transactions) {
                        transactionProvider.getTransactionReceiptAsync(transaction.getHash())
                                .thenAccept(transactionReceipt -> {
                                    List<EventValue> eventValues;
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

                });
    }

    private void publishOwnerBalance(final Contract contract, final List<Transaction> transactions, final EthBlock.Block block) {
        final BigInteger value = getValueFor(contract.getOwnerAddress(), transactions);
        balanceProvider.getBalanceAsync(contract.getOwnerAddress())
                .thenAccept(balance -> {
                    eventPublisher.publish(new OwnerBalanceChangedEvent(
                            block,
                            contract,
                            value,
                            balance
                    ));
                });
    }

    private BigInteger getValueFor(String address, List<Transaction> transactions) {
        BigInteger result = BigInteger.ZERO;
        for (Transaction transaction : transactions) {
            if (address.compareToIgnoreCase(transaction.getFrom()) == 0) {
                result = result.subtract(transaction.getValue());
            }
            else if (address.compareToIgnoreCase(transaction.getTo()) == 0) {
                result = result.add(transaction.getValue());
            }
            else {
                log.error("There is no such address {} in from or to field of transaction {}.", address, transaction.getHash(), new Exception("Wrong address or transaction."));
            }
        }
        return result;
    }
}
