package io.lastwill.eventscan.services.monitors;

import io.lastwill.eventscan.events.ContractEventsEvent;
import io.lastwill.eventscan.events.contract.erc20.TransferEvent;
import io.mywish.wrapper.ContractEvent;
import io.lastwill.eventscan.model.Contract;
import io.lastwill.eventscan.repositories.ContractRepository;
import io.lastwill.eventscan.services.EventParser;
import io.lastwill.eventscan.services.TransactionProvider;
import io.mywish.wrapper.WrapperBlock;
import io.mywish.wrapper.WrapperLog;
import io.mywish.wrapper.WrapperTransaction;
import io.lastwill.eventscan.model.NetworkType;
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
    private void onNewBlockEvent(final NewBlockEvent event) {
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

        // TODO remove
        Contract ct = new Contract();
        ct.setAddress("0x2a464486dc73e90bcf9fa8125622fb0788ca385c");
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
            transactionProvider.getTransactionReceiptAsync(networkType, transaction)
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
        transactionProvider.getTransactionReceiptAsync(networkType, transaction)
                .thenAccept(transactionReceipt -> {
                    List<ContractEvent> events;
                    try {
                        events = eventParser.parseEvents(transactionReceipt);
                    }
                    catch (Throwable e) {
                        log.error("Error on parsing events.", e);
                        return;
                    }
                    if (events.isEmpty()) {
                        return;
                    }
                    eventPublisher.publish(
                            new ContractEventsEvent(
                                    networkType,
                                    contract,
                                    events,
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
