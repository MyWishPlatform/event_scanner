package io.lastwill.eventscan.services.monitors.ethbnbswap;

import io.lastwill.eventscan.events.model.contract.BnbWishPutEvent;
import io.lastwill.eventscan.events.model.wishbnbswap.EthBnbProfile;
import io.lastwill.eventscan.events.model.wishbnbswap.ProfileStorage;
import io.lastwill.eventscan.model.NetworkType;
import io.lastwill.eventscan.model.EthToBnbLinkEntry;
import io.lastwill.eventscan.repositories.EthToBnbLinkEntryRepository;
import io.lastwill.eventscan.services.TransactionProvider;
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

    private EthBnbProfile ethBnbProfile;


    @EventListener
    public void onLink(final NewBlockEvent newBlockEvent) {
        if (newBlockEvent.getNetworkType() != NetworkType.ETHEREUM_MAINNET) {
            return;
        }

        newBlockEvent.getTransactionsByAddress()
                .entrySet()
                .stream()
                .filter(entry -> profileStorage.getEthBnbProfiles()
                        .stream()
                        .map(EthBnbProfile::getWishLinkAddress)
                        .collect(Collectors.toList())
                        .contains(entry.getKey()))
                .map(entry -> {
                    this.ethBnbProfile = profileStorage.getEthBnbProfiles()
                            .stream()
                            .filter(profile -> profile.getEthTokenAddress().equals(entry.getKey()))
                            .findFirst()
                            .get();
                    return entry.getValue();
                })
                .flatMap(Collection::stream)
                .forEach(transaction -> transactionProvider.getTransactionReceiptAsync(newBlockEvent.getNetworkType(), transaction)
                        .thenAccept(receipt -> receipt.getLogs()
                                .stream()
                                .filter(event -> event instanceof BnbWishPutEvent)
                                .map(event -> (BnbWishPutEvent) event)
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
                                })
                                .filter(Objects::nonNull)
                                .map(linkRepository::save)
                                .forEach(linkEntry -> log.info("Linked \"{} \"{} : {}\"",
                                        linkEntry.getSymbol(), linkEntry.getEthAddress(), linkEntry.getBnbAddress()))
                        )
                );
    }
}
