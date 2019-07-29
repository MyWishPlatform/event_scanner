package io.lastwill.eventscan.services.monitors.ethbnbswap;

import io.lastwill.eventscan.events.model.contract.erc20.TransferEvent;
import io.lastwill.eventscan.events.model.wishbnbswap.EthBnbProfile;
import io.lastwill.eventscan.events.model.wishbnbswap.ProfileStorage;
import io.lastwill.eventscan.events.model.wishbnbswap.TokensBurnedEvent;
import io.lastwill.eventscan.model.*;
import io.lastwill.eventscan.repositories.EthToBnbLinkEntryRepository;
import io.lastwill.eventscan.repositories.EthToBnbSwapEntryRepository;
import io.lastwill.eventscan.services.Bep2WishSender;
import io.lastwill.eventscan.services.TransactionProvider;
import io.mywish.blockchain.WrapperTransaction;
import io.mywish.scanner.model.NewBlockEvent;
import io.mywish.scanner.services.EventPublisher;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;

import java.math.BigInteger;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Slf4j

public class BurnMonitor {
    @Autowired
    private EventPublisher eventPublisher;

    @Autowired
    private TransactionProvider transactionProvider;

    @Autowired
    private EthToBnbLinkEntryRepository linkRepository;

    @Autowired
    private EthToBnbSwapEntryRepository swapRepository;

    @Autowired
    private Bep2WishSender wishSender;

    @Autowired
    private ProfileStorage profileStorage;

    private CryptoCurrency ethCoin;

    private CryptoCurrency bnbCoin;

    private String burnerAddress;

    @EventListener
    public void onBurn(final NewBlockEvent newBlockEvent) {
        if (newBlockEvent.getNetworkType() != NetworkType.ETHEREUM_MAINNET) {
            return;
        }
        Map<String, List<WrapperTransaction>> entries = filterTransactionsByTokenAddress(newBlockEvent);
        if (entries == null) return;
        for (Map.Entry<String, List<WrapperTransaction>> entry : entries.entrySet()) {
            initEthToBnbProfile(entry.getKey());
            for (WrapperTransaction transaction : entry.getValue()) {
                List<TransferEvent> transferEvents = sortTransactionsByEvent(transaction, newBlockEvent);
                Stream<EthToBnbSwapEntry> swapEntries = publishEvent(transferEvents, transaction);
                sendToken(swapEntries);
            }
        }
    }

    private void initEthToBnbProfile(String ethToBnbTokenAddress) {
        EthBnbProfile ethBnbProfile = profileStorage.getEthBnbProfiles()
                .stream()
                .filter(profile -> profile.getEthTokenAddress().equals(ethToBnbTokenAddress))
                .findFirst()
                .get();
        ethCoin = ethBnbProfile.getEth();
        burnerAddress = ethBnbProfile.getEthBurnerAddress();
        bnbCoin = ethBnbProfile.getBnb();
    }

    private Map<String, List<WrapperTransaction>> filterTransactionsByTokenAddress(NewBlockEvent event) {
        return event.getTransactionsByAddress()
                .entrySet()
                .stream()
                .filter(entry -> profileStorage.getEthBnbProfiles()
                        .stream()
                        .map(EthBnbProfile::getEthTokenAddress)
                        .collect(Collectors.toList())
                        .contains(entry.getKey()))
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
    }

    private List<TransferEvent> sortTransactionsByEvent(WrapperTransaction transaction, NewBlockEvent newBlockEvent) {
        List<TransferEvent> transferEvents = new ArrayList<>();
        transactionProvider.getTransactionReceiptAsync(newBlockEvent.getNetworkType(), transaction)
                .thenAccept(receipt -> receipt.getLogs()
                        .stream()
                        .filter(event -> event instanceof TransferEvent)
                        .map(event -> (TransferEvent) event)
                        .filter(event -> burnerAddress.equalsIgnoreCase(event.getTo()))
                        .forEach(transferEvents::add));
        return transferEvents;
    }

    private Stream<EthToBnbSwapEntry> publishEvent(List<TransferEvent> events, WrapperTransaction transaction) {
        return events
                .stream()
                .map(transferEvent -> {
                    String ethAddress = transferEvent.getFrom().toLowerCase();
                    BigInteger amount = convertEthToBnb(transferEvent.getTokens());
                    String bnbAddress = null;

                    EthToBnbLinkEntry linkEntry = linkRepository.findByEthAddress(ethAddress);
                    if (linkEntry != null) {
                        bnbAddress = linkEntry.getBnbAddress();
                    } else {
                        log.warn("\"{}\" not linked", ethAddress);
                    }

                    EthToBnbSwapEntry swapEntry = swapRepository.findByEthTxHash(transaction.getHash());
                    if (swapEntry != null) {
                        log.warn("Swap entry already in DB: {}", transaction.getHash());
                        return null;
                    }
                    swapEntry = new EthToBnbSwapEntry(
                            linkEntry,
                            amount,
                            transaction.getHash()
                    );
                    swapEntry = swapRepository.save(swapEntry);

                    log.info("{} burned {} {}", ethAddress, wishSender.toString(amount), ethCoin);

                    eventPublisher.publish(new TokensBurnedEvent(
                            ethCoin.getSymbol(),
                            bnbCoin.getDecimals(),
                            swapEntry,
                            ethAddress,
                            bnbAddress
                    ));
                    return swapEntry;
                });
    }

    private void sendToken(Stream<EthToBnbSwapEntry> swapEntries) {
        swapEntries
                .filter(Objects::nonNull)
                .forEach(wishSender::send);
    }

    protected BigInteger convertEthToBnb(BigInteger amount) {
        int ethWishDecimals = ethCoin.getDecimals();
        int bnbWishDecimals = bnbCoin.getDecimals();

        return amount
                .multiply(BigInteger.TEN.pow(bnbWishDecimals))
                .divide(BigInteger.TEN.pow(ethWishDecimals));
    }
}
