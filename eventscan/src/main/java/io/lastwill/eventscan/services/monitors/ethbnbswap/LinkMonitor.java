package io.lastwill.eventscan.services.monitors.ethbnbswap;

import io.lastwill.eventscan.events.model.contract.BnbWishPutEvent;
import io.lastwill.eventscan.model.EthBnbProfile;
import io.lastwill.eventscan.model.ProfileStorage;
import io.lastwill.eventscan.model.NetworkType;
import io.lastwill.eventscan.model.EthToBnbLinkEntry;
import io.lastwill.eventscan.repositories.EthToBnbLinkEntryRepository;
import io.lastwill.eventscan.services.TransactionProvider;
import io.mywish.blockchain.WrapperTransaction;
import io.mywish.scanner.model.NewBlockEvent;
import lombok.extern.slf4j.Slf4j;
import org.bouncycastle.util.Arrays;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Component
public class LinkMonitor {

    @Autowired
    private TransactionProvider transactionProvider;

    @Autowired
    private EthToBnbLinkEntryRepository linkRepository;

    @Autowired
    private ProfileStorage profileStorage;

    @EventListener
    public void onLink(final NewBlockEvent newBlockEvent) {

        if (newBlockEvent.getNetworkType() != NetworkType.ETHEREUM_MAINNET) {
            return;
        }

        Map<String, List<WrapperTransaction>> entries = filterTransactionsByAddress(newBlockEvent);

        if (entries == null || entries.size() == 0) return;

        for (Map.Entry<String, List<WrapperTransaction>> entry : entries.entrySet()) {
            EthBnbProfile ethBnbProfile;
            try {
                 ethBnbProfile = profileStorage.getProfileByEthLinkAddress(entry.getKey());
            } catch (NoSuchElementException ex) {
                log.error(ex.getMessage());
                continue;
            }
            for (WrapperTransaction transaction : entry.getValue()) {
                List<BnbWishPutEvent> bnbWishPutEvents = new ArrayList<>();
                transactionProvider.getTransactionReceiptAsync(newBlockEvent.getNetworkType(), transaction)
                        .thenAccept(receipt -> {
                            receipt.getLogs()
                                    .stream()
                                    .filter(event -> event instanceof BnbWishPutEvent)
                                    .forEach(event -> bnbWishPutEvents.add((BnbWishPutEvent) event));
                            List<EthToBnbLinkEntry> ethToBnbLinkEntries = getEthToBnbLinkEntries(bnbWishPutEvents, transaction, ethBnbProfile);
                            saveLinkEntries(ethToBnbLinkEntries);
                        });
            }
        }
    }

    private Map<String, List<WrapperTransaction>> filterTransactionsByAddress(NewBlockEvent newBlockEvent) {
        return newBlockEvent.getTransactionsByAddress()
                .entrySet()
                .stream()
                .filter(entry -> profileStorage.getEthBnbProfiles()
                        .stream()
                        .map(EthBnbProfile::getEthLinkAddress)
                        .collect(Collectors.toList())
                        .contains(entry.getKey()))
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
    }


    private List<EthToBnbLinkEntry> getEthToBnbLinkEntries(List<BnbWishPutEvent> events, WrapperTransaction transaction, EthBnbProfile ethBnbProfile) {
        return events
                .stream()
                .map(putEvent -> {
                    String eth = putEvent.getEth().toLowerCase();
                    byte[] input = transaction.getOutputs().get(0).getRawOutputScript();
                    byte[] bnbBytes = Arrays.copyOfRange(input, input.length - 64, input.length);
                    String bnb = new String(bnbBytes).trim();

                    if (linkRepository.existsByEthAddressAndSymbol(eth, ethBnbProfile.getEth().name())) {
                        log.warn("\"{} : {} - {}\" already linked.", ethBnbProfile.getEth().name(), eth, bnb);
                        return null;
                    }
                    return new EthToBnbLinkEntry(ethBnbProfile.getEth().name(), eth, bnb);
                }).collect(Collectors.toList());
    }

    private void saveLinkEntries(List<EthToBnbLinkEntry> linkEntries) {
        linkEntries
                .stream()
                .filter(Objects::nonNull)
                .map(linkRepository::save)
                .forEach(linkEntry -> log.info("Linked \"{} \"{} : {}\"",
                        linkEntry.getSymbol(), linkEntry.getEthAddress(), linkEntry.getBnbAddress()));
    }
}
