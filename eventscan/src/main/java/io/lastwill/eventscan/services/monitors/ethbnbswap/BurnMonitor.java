package io.lastwill.eventscan.services.monitors.ethbnbswap;

import io.lastwill.eventscan.events.model.contract.erc20.TransferEvent;
import io.lastwill.eventscan.events.model.wishbnbswap.AddressByCoin;
import io.lastwill.eventscan.events.model.wishbnbswap.TokensBurnedEvent;
import io.lastwill.eventscan.model.*;
import io.lastwill.eventscan.repositories.EthToBnbLinkEntryRepository;
import io.lastwill.eventscan.repositories.EthToBnbSwapEntryRepository;
import io.lastwill.eventscan.services.Bep2WishSender;
import io.lastwill.eventscan.services.TransactionProvider;
import io.mywish.scanner.model.NewBlockEvent;
import io.mywish.scanner.services.EventPublisher;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;

import java.math.BigInteger;
import java.util.Collection;
import java.util.Map;
import java.util.Objects;

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
    private AddressByCoin addressByCoin;

    private String coin;

    private String burnerAddress;



    @EventListener
    public void onBurn(final NewBlockEvent newBlockEvent) {
        if (newBlockEvent.getNetworkType() != NetworkType.ETHEREUM_MAINNET) {
            return;
        }

        newBlockEvent.getTransactionsByAddress()
                .entrySet()
                .stream()
                .filter(entry -> addressByCoin.getTokenAddressByCoin().keySet().contains(entry.getKey()))
                .map(entry -> {
                    coin = addressByCoin.getTokenAddressByCoin().get(entry.getKey()).name();
                    burnerAddress = addressByCoin.getBurnerAddressByCoin().get(AddressByCoin.SwapsCoin.valueOf(coin));
                    return entry.getValue();
                })
                .flatMap(Collection::stream)
                .forEach(transaction -> transactionProvider.getTransactionReceiptAsync(newBlockEvent.getNetworkType(), transaction)
                        .thenAccept(receipt -> receipt.getLogs()
                                .stream()
                                .filter(event -> event instanceof TransferEvent)
                                .map(event -> (TransferEvent) event)
                                .filter(event -> burnerAddress.equalsIgnoreCase(event.getTo()))
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

                                    swapEntry = this.generateSwapEntry(
                                            linkEntry,
                                            amount,
                                            transaction.getHash()
                                    );
                                    swapEntry = swapRepository.save(swapEntry);

                                    log.info("{} burned {} {}", ethAddress, wishSender.toString(amount), coin);

                                    eventPublisher.publish(new TokensBurnedEvent(
                                            coin,
                                            CryptoCurrency.BWISH.getDecimals(),//BWISH
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

    protected BigInteger convertEthToBnb(BigInteger amount) {
        int ethWishDecimals = CryptoCurrency.WISH.getDecimals();
        int bnbWishDecimals = CryptoCurrency.BWISH.getDecimals();

        return amount
                .multiply(BigInteger.TEN.pow(bnbWishDecimals))
                .divide(BigInteger.TEN.pow(ethWishDecimals));
    }

    protected  EthToBnbSwapEntry generateSwapEntry(EthToBnbLinkEntry linkEntry, BigInteger amount, String ethTxHash) {
        return new EthToBnbSwapEntry(linkEntry, amount, ethTxHash);
    }
}
