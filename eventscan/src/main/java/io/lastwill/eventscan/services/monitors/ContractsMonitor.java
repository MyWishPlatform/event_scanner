package io.lastwill.eventscan.services.monitors;

import io.lastwill.eventscan.events.ContractEventsEvent;
import io.lastwill.eventscan.events.contract.ContractEvent;
import io.lastwill.eventscan.model.Contract;
import io.lastwill.eventscan.repositories.ContractRepository;
import io.lastwill.eventscan.services.EventParser;
import io.lastwill.eventscan.services.TransactionProvider;
import io.mywish.scanner.WrapperBlock;
import io.mywish.scanner.WrapperLog;
import io.mywish.scanner.WrapperTransaction;
import io.mywish.scanner.model.NetworkType;
import io.mywish.scanner.model.NewBlockEvent;
import io.mywish.scanner.services.EventPublisher;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import org.springframework.util.MultiValueMap;

import javax.annotation.PostConstruct;
import java.util.*;

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

    @Value("${io.lastwill.eventscan.contract.proxy-address.ethereum}")
    private String proxyAddressEthereum;
    @Value("${io.lastwill.eventscan.contract.proxy-address.ropsten}")
    private String proxyAddressRopsten;

    private final HashMap<NetworkType, String> proxyByNetwork = new HashMap<>();

    @PostConstruct
    protected void init() {
        proxyByNetwork.put(NetworkType.ETHEREUM_MAINNET, proxyAddressEthereum.toLowerCase());
        proxyByNetwork.put(NetworkType.ETHEREUM_ROPSTEN, proxyAddressRopsten.toLowerCase());
    }

    @EventListener
    public void onNewBlock(final NewBlockEvent event) {
        Set<String> addresses = event.getTransactionsByAddress().keySet();
        if (addresses.isEmpty()) {
            return;
        }

        if (proxyByNetwork.containsKey(event.getNetworkType())) {
            final String proxyAddress = proxyByNetwork.get(event.getNetworkType());

            if (addresses.contains(proxyAddress)) {
                final List<WrapperTransaction> transactions = event.getTransactionsByAddress().get(proxyAddress);
                grabProxyEvents(event.getNetworkType(), transactions, event.getBlock());
            }
        }

        Contract ct = new Contract();
        ct.setAddress("0xaf4ffcd346eb4c4950749ce2095b6d26b7496d2f");
        ct.setId(0);
        List<Contract> contracts = contractRepository.findByAddressesList(addresses, event.getNetworkType());
        contracts.add(ct);
        for (final Contract contract : contracts) {
            if (contract.getAddress() == null || !addresses.contains(contract.getAddress().toLowerCase())) {
                continue;
            }

            final List<WrapperTransaction> transactions = event.getTransactionsByAddress().get(contract.getAddress().toLowerCase());
            for (final WrapperTransaction transaction : transactions) {
                // grab events
                if (transaction.getOutputs().size() == 0) {
                    continue;
                }

                grabContractEvents(event.getNetworkType(), contract, transaction, event.getBlock());
            }
        }
    }

    private void grabProxyEvents(final NetworkType networkType, final List<WrapperTransaction> transactions, final WrapperBlock block) {
        for (WrapperTransaction transaction : transactions) {
            transactionProvider.getTransactionReceiptAsync(networkType, transaction.getHash())
                    .thenAccept(transactionReceipt -> {
                        MultiValueMap<String, WrapperLog> logsByAddress = CollectionUtils.toMultiValueMap(new HashMap<>());
                        for (WrapperLog log : transactionReceipt.getLogs()) {
                            logsByAddress.add(log.getAddress(), log);
                        }

                        for (Contract contract : contractRepository.findByAddressesList(logsByAddress.keySet(), networkType)) {
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
                                            networkType,
                                            contract,
                                            eventValues,
                                            transaction,
                                            transactionReceipt,
                                            block)
                            );
                        }

                    })
                    .exceptionally(throwable -> {
                        log.error("ContractEventsEvent handling cause exception.", throwable);
                        return null;
                    });
        }
    }

    private void grabContractEvents(final NetworkType networkType, final Contract contract, final WrapperTransaction transaction, final WrapperBlock block) {
        transactionProvider.getTransactionReceiptAsync(networkType, transaction.getHash())
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
                                    networkType,
                                    contract,
                                    eventValues,
                                    transaction,
                                    transactionReceipt,
                                    block)
                    );
                })
                .exceptionally(throwable -> {
                    log.error("ContractEventsEvent handling cause exception.", throwable);
                    return null;
                });
    }
}
