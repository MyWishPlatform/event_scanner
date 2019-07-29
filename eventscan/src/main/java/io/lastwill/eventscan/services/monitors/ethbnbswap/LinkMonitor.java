package io.lastwill.eventscan.services.monitors.ethbnbswap;

import io.lastwill.eventscan.events.model.contract.BnbWishPutEvent;
import io.lastwill.eventscan.events.model.wishbnbswap.EthBnbProfile;
import io.lastwill.eventscan.events.model.wishbnbswap.ProfileStorage;
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
import java.util.stream.Stream;

@Slf4j
@Component
public class LinkMonitor {

    @Autowired
    private TransactionProvider transactionProvider;

    @Autowired
    private EthToBnbLinkEntryRepository linkRepository;

    @Autowired
    private ProfileStorage profileStorage;

    private EthBnbProfile ethBnbProfile;

    @EventListener
    public void onLink(final NewBlockEvent newBlockEvent) {
        if (newBlockEvent.getNetworkType() != NetworkType.ETHEREUM_MAINNET) {
            return;
        }
        Map<String, List<WrapperTransaction>> entries = filterTransactionsByAddress(newBlockEvent);

        if(entries == null) return;
        for (Map.Entry<String, List<WrapperTransaction>> entry : entries.entrySet()) {
            try {
                initEthBnbProfile(entry.getKey());
            } catch (NoSuchElementException ex) {
                log.error(ex.getMessage());
                continue;
            }
            for (WrapperTransaction transaction : entry.getValue()) {
                List<BnbWishPutEvent> events = sortTransactionsByEvent(transaction, newBlockEvent.getNetworkType());
                Stream <EthToBnbLinkEntry> ethToBnbLinkEntries = getEthToBnbLinkEntries(events, transaction);
                saveEntries(ethToBnbLinkEntries);
            }
        }
    }

    private Map<String, List<WrapperTransaction>> filterTransactionsByAddress(NewBlockEvent newBlockEvent) {

        return newBlockEvent.getTransactionsByAddress()
                .entrySet()
                .stream()
                .filter(entry -> profileStorage.getEthBnbProfiles()
                        .stream()
                        .map(EthBnbProfile::getWishLinkAddress)
                        .collect(Collectors.toList())
                        .contains(entry.getKey()))
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
    }
    private void initEthBnbProfile(String ethAddress) {
        this.ethBnbProfile = profileStorage.getProfileByEthTokenAddress(ethAddress);
    }

    private List<BnbWishPutEvent> sortTransactionsByEvent(WrapperTransaction transaction, NetworkType type) {
        List<BnbWishPutEvent> result = new ArrayList<>();
        transactionProvider.getTransactionReceiptAsync(type, transaction)
                .thenAccept(receipt -> receipt.getLogs()
                        .stream()
                        .filter(event -> event instanceof BnbWishPutEvent)
                        .forEach(e -> result.add((BnbWishPutEvent) e)));
        return result;
    }

    private Stream<EthToBnbLinkEntry> getEthToBnbLinkEntries(List<BnbWishPutEvent> events, WrapperTransaction transaction) {
            return events
                .stream()
                .map(putEvent -> {
                    String eth = putEvent.getEth().toLowerCase();
                    byte[] input = transaction.getOutputs().get(0).getRawOutputScript();
                    byte[] bnbBytes = Arrays.copyOfRange(input, input.length - 64, input.length);
                    String bnb = new String(bnbBytes).trim();

                    if (linkRepository.existsByEthAddressAndSymbol(eth, ethBnbProfile.getEth().getSymbol())) {
                        log.warn("\"{} : {} - {}\" already linked.", ethBnbProfile.getEth().getSymbol(), eth, bnb);
                        return null;
                    }
                    return new EthToBnbLinkEntry(ethBnbProfile.getEth().getSymbol(), eth, bnb);
                });
    }

    private void saveEntries(Stream<EthToBnbLinkEntry> entryStream) {
        entryStream.filter(Objects::nonNull)
                .map(linkRepository::save)
                .forEach(linkEntry -> log.info("Linked \"{} \"{} : {}\"",
                        linkEntry.getSymbol(), linkEntry.getEthAddress(), linkEntry.getBnbAddress()));
    }
}
