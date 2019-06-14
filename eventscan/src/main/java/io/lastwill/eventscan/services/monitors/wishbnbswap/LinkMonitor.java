package io.lastwill.eventscan.services.monitors.wishbnbswap;

import io.lastwill.eventscan.events.model.contract.BnbWishPutEvent;
import io.lastwill.eventscan.model.NetworkType;
import io.lastwill.eventscan.model.WishToBnbLinkEntry;
import io.lastwill.eventscan.repositories.WishToBnbLinkEntryRepository;
import io.lastwill.eventscan.services.TransactionProvider;
import io.mywish.scanner.model.NewBlockEvent;
import lombok.extern.slf4j.Slf4j;
import org.bouncycastle.util.Arrays;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.Map;
import java.util.Objects;

@Slf4j
@Component
public class LinkMonitor {
    @Autowired
    private TransactionProvider transactionProvider;

    @Autowired
    private WishToBnbLinkEntryRepository linkRepository;

    @Value("${io.lastwill.eventscan.binance.wish-swap.linker-address}")
    private String linkerAddress;

    @EventListener
    public void onLink(final NewBlockEvent newBlockEvent) {
        if (newBlockEvent.getNetworkType() != NetworkType.ETHEREUM_MAINNET) {
            return;
        }

        newBlockEvent.getTransactionsByAddress()
                .entrySet()
                .stream()
                .filter(entry -> linkerAddress.equalsIgnoreCase(entry.getKey()))
                .map(Map.Entry::getValue)
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

                                    if (linkRepository.existsByEthAddress(eth)) {
                                        log.warn("\"{} : {}\" already linked.", eth, bnb);
                                        return null;
                                    }

                                    return new WishToBnbLinkEntry(eth, bnb);
                                })
                                .filter(Objects::nonNull)
                                .map(linkRepository::save)
                                .forEach(linkEntry -> log.info("Linked \"{} : {}\"", linkEntry.getEthAddress(), linkEntry.getBnbAddress()))
                        )
                );
    }
}
