package io.lastwill.eventscan.services.monitors.wishbnbswap;

import io.lastwill.eventscan.events.model.contract.erc20.TransferEvent;
import io.lastwill.eventscan.events.model.wishbnbswap.TokensBurnedEvent;
import io.lastwill.eventscan.model.CryptoCurrency;
import io.lastwill.eventscan.model.NetworkType;
import io.lastwill.eventscan.model.WishToBnbLinkEntry;
import io.lastwill.eventscan.model.WishToBnbSwapEntry;
import io.lastwill.eventscan.repositories.WishToBnbLinkEntryRepository;
import io.lastwill.eventscan.repositories.WishToBnbSwapEntryRepository;
import io.lastwill.eventscan.services.Bep2WishSender;
import io.lastwill.eventscan.services.TransactionProvider;
import io.mywish.scanner.model.NewBlockEvent;
import io.mywish.scanner.services.EventPublisher;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import java.math.BigInteger;
import java.util.Collection;
import java.util.Map;
import java.util.Objects;

@Slf4j
@Component
public class BurnMonitor {
    @Autowired
    private EventPublisher eventPublisher;

    @Autowired
    private TransactionProvider transactionProvider;

    @Autowired
    private WishToBnbLinkEntryRepository linkRepository;

    @Autowired
    private WishToBnbSwapEntryRepository swapRepository;

    @Autowired
    private Bep2WishSender wishSender;

    @Value("${io.lastwill.eventscan.binance.wish-swap.burner-address}")
    private String burnerAddress;

    @Value("${io.lastwill.eventscan.contract.token-address.wish}")
    private String tokenAddressWish;

    @EventListener
    public void onBurn(final NewBlockEvent newBlockEvent) {
        if (newBlockEvent.getNetworkType() != NetworkType.ETHEREUM_MAINNET) {
            return;
        }

        newBlockEvent.getTransactionsByAddress()
                .entrySet()
                .stream()
                .filter(entry -> tokenAddressWish.equalsIgnoreCase(entry.getKey()))
                .map(Map.Entry::getValue)
                .flatMap(Collection::stream)
                .forEach(transaction -> transactionProvider.getTransactionReceiptAsync(newBlockEvent.getNetworkType(), transaction)
                        .thenAccept(receipt -> receipt.getLogs()
                                .stream()
                                .filter(event -> event instanceof TransferEvent)
                                .map(event -> (TransferEvent) event)
                                .filter(event -> burnerAddress.equalsIgnoreCase(event.getTo()))
                                .map(transferEvent -> {
                                    String ethAddress = transferEvent.getFrom().toLowerCase();
                                    BigInteger amount = convertEthWishToBnbWish(transferEvent.getTokens());
                                    String bnbAddress = null;

                                    WishToBnbLinkEntry linkEntry = linkRepository.findByEthAddress(ethAddress);
                                    if (linkEntry != null) {
                                        bnbAddress = linkEntry.getBnbAddress();
                                    } else {
                                        log.warn("\"{}\" not linked", ethAddress);
                                    }

                                    WishToBnbSwapEntry swapEntry = swapRepository.findByEthTxHash(transaction.getHash());
                                    if (swapEntry != null) {
                                        log.warn("Swap entry already in DB: {}", transaction.getHash());
                                        return null;
                                    }

                                    swapEntry = new WishToBnbSwapEntry(
                                            linkEntry,
                                            amount,
                                            transaction.getHash()
                                    );
                                    swapEntry = swapRepository.save(swapEntry);

                                    log.info("{} burned {} WISH", ethAddress, wishSender.toString(amount));

                                    eventPublisher.publish(new TokensBurnedEvent(
                                            "WISH",
                                            CryptoCurrency.BWISH.getDecimals(),
                                            swapEntry,
                                            ethAddress,
                                            bnbAddress
                                    ));

                                    return swapEntry;
                                })
                                .filter(Objects::nonNull)
                                .forEach(wishSender::send)
                        )
                );
    }

    private BigInteger convertEthWishToBnbWish(BigInteger amount) {
        int ethWishDecimals = CryptoCurrency.WISH.getDecimals();
        int bnbWishDecimals = CryptoCurrency.BWISH.getDecimals();

        return amount
                .multiply(BigInteger.TEN.pow(bnbWishDecimals))
                .divide(BigInteger.TEN.pow(ethWishDecimals));
    }
}
