package io.lastwill.eventscan.services.monitors;

import io.lastwill.eventscan.events.ContractEventsEvent;
import io.lastwill.eventscan.events.contract.ContractEvent;
import io.lastwill.eventscan.model.Contract;
import io.lastwill.eventscan.repositories.ContractRepository;
import io.lastwill.eventscan.services.EventParser;
import io.lastwill.eventscan.services.TransactionProvider;
import io.mywish.scanner.model.NetworkType;
import io.mywish.scanner.model.NewNeoBlockEvent;
import io.mywish.scanner.model.NewWeb3BlockEvent;
import io.mywish.scanner.services.EventPublisher;
import io.mywish.scanner.services.scanners.NeoScanner;
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
import javax.xml.bind.DatatypeConverter;
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

    @Autowired
    private NeoScanner neoScanner;

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
    public void onNewWeb3Block(final NewWeb3BlockEvent event) {
        Set<String> addresses = event.getTransactionsByAddress().keySet();
        if (addresses.isEmpty()) {
            return;
        }

        if (proxyByNetwork.containsKey(event.getNetworkType())) {
            final String proxyAddress = proxyByNetwork.get(event.getNetworkType());

            if (addresses.contains(proxyAddress)) {
                final List<Transaction> transactions = event.getTransactionsByAddress().get(proxyAddress);
                grabProxyEvents(event.getNetworkType(), transactions, event.getBlock());
            }
        }

        List<Contract> contracts = contractRepository.findByAddressesList(addresses, event.getNetworkType());
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

                grabContractEvents(event.getNetworkType(), contract, transaction, event.getBlock());
            }
        }
    }

    @EventListener
    public void onNewNeoBlock(final NewNeoBlockEvent event) throws java.io.IOException {
        for (com.glowstick.neocli4j.Transaction tx : event.getBlock().getTransactions()) {
            if (tx.getType() != com.glowstick.neocli4j.Transaction.Type.InvocationTransaction) continue;
            if (tx.getContracts().size() == 0) {
                // contract creation (?)
            }
            Map<String, List<ContractEvent>> contractEvents = new HashMap<>();
            // TODO change
            neoScanner.getEvents(tx.getHash()).forEach(contractEvent -> {
                // TODO load from database
                if (!"0x8586b9a5fe48958aaf51fe226925f205b9273d43".equals(contractEvent.getContract())) return;
                contractEvents.computeIfAbsent(contractEvent.getContract(), x -> new ArrayList<>()).add(eventParser.parseEventNeo(contractEvent));
                System.out.println(tx.getHash() + ": contract called(" + contractEvent.getContract() + ")");
            });
            EthBlock.Block block = new EthBlock.Block();
            block.setHash(event.getBlock().getHash());
            block.setNumber("0x" + Long.toHexString(event.getBlockNumber()));
            System.out.println(event.getBlockNumber());
            System.out.println(block.getNumber().longValue());
            contractEvents.keySet().forEach(contractAddress -> {
                Contract contract = new Contract();
                contract.setAddress(contractAddress);
                System.out.println("publishing");
                eventPublisher.publish(new ContractEventsEvent(
                        event.getNetworkType(),
                        contract,
                        contractEvents.get(contractAddress),
                        null,
                        null,
                        block
                ));
            });
        }
    }

    private void grabProxyEvents(final NetworkType networkType, final List<Transaction> transactions, final EthBlock.Block block) {
        for (Transaction transaction : transactions) {
            transactionProvider.getTransactionReceiptAsync(networkType, transaction.getHash())
                    .thenAccept(transactionReceipt -> {
                        MultiValueMap<String, Log> logsByAddress = CollectionUtils.toMultiValueMap(new HashMap<>());
                        for (Log log : transactionReceipt.getLogs()) {
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

    private void grabContractEvents(final NetworkType networkType, final Contract contract, final Transaction transaction, final EthBlock.Block block) {
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
