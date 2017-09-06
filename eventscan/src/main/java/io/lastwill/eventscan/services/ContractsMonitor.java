package io.lastwill.eventscan.services;

import io.lastwill.eventscan.events.ContractBalanceChangedEvent;
import io.lastwill.eventscan.events.NewBlockEvent;
import io.lastwill.eventscan.events.OwnerBalanceChangedEvent;
import io.lastwill.eventscan.model.Contract;
import io.lastwill.eventscan.repositories.ContractRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import org.web3j.protocol.core.methods.response.EthBlock;
import org.web3j.protocol.core.methods.response.Transaction;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.math.BigInteger;
import java.util.HashSet;
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

        List<Contract> contracts = contractRepository.findByAddressesList(addresses);
        for (Contract contract : contracts) {
            boolean wasPublished = false;
            if (addresses.contains(contract.getAddress().toLowerCase())) {
                final List<Transaction> transactions = newBlockEvent.getTransactionsByAddress().get(contract.getAddress().toLowerCase());
                publishContractBalance(contract, transactions, newBlockEvent.getBlock());
                wasPublished |= true;
            }
            if (addresses.contains(contract.getOwnerAddress().toLowerCase())) {
                final List<Transaction> transactions = newBlockEvent.getTransactionsByAddress().get(contract.getOwnerAddress().toLowerCase());
                publishOwnerBalance(contract, transactions, newBlockEvent.getBlock());
                wasPublished |= true;
            }

            if (!wasPublished) {
                log.warn("Contract {} was mentioned in block because of unknown reason.", contract.getId());
            }
        }
    }

    private void publishContractBalance(final Contract contract, final List<Transaction> transactions, final EthBlock.Block block) {
        final BigInteger value = getValueFor(contract.getAddress(), transactions);
        balanceProvider.getBalanceAsync(contract.getAddress())
                .thenAccept(balance -> {
                    eventPublisher.publish(new ContractBalanceChangedEvent(
                            block,
                            contract,
                            value,
                            balance
                    ));
                });
    }

    private void publishOwnerBalance(final Contract contract, final List<Transaction> transactions, final EthBlock.Block block) {
        final BigInteger value = getValueFor(contract.getOwnerAddress(), transactions);
        BigInteger balance = null;
        try {
            balance = balanceProvider.getBalance(contract.getOwnerAddress());
        }
        catch (IOException e) {
            log.error("er", e);
        }
        eventPublisher.publish(new OwnerBalanceChangedEvent(
                block,
                contract,
                value,
                balance
        ));

//        balanceProvider.getBalanceAsync(contract.getOwnerAddress())
//                .thenAccept(balance -> {
//                    eventPublisher.publish(new OwnerBalanceChangedEvent(
//                            block,
//                            contract,
//                            value,
//                            balance
//                    ));
//                });
    }

    private BigInteger getValueFor(String address, List<Transaction> transactions) {
        BigInteger result = BigInteger.ZERO;
        for (Transaction transaction: transactions) {
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
