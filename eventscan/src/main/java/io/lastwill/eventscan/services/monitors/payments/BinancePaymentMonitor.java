package io.lastwill.eventscan.services.monitors.payments;

import io.lastwill.eventscan.events.model.UserPaymentEvent;
import io.lastwill.eventscan.model.CryptoCurrency;
import io.lastwill.eventscan.model.NetworkType;
import io.lastwill.eventscan.model.UserSiteBalance;
import io.lastwill.eventscan.repositories.UserSiteBalanceRepository;
import io.mywish.binance.blockchain.model.WrapperOutputBinance;
import io.mywish.scanner.model.NewBlockEvent;
import io.mywish.scanner.services.EventPublisher;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.*;

@Slf4j
@Component
public class BinancePaymentMonitor {
    @Value("${io.lastwill.eventscan.binance.target-address}")
    private String targetAddress;
    @Value("${io.lastwill.eventscan.binance.token-symbol}")
    private String binanceSymbol;
    @Value("${io.lastwill.eventscan.binance-wish.token-symbol}")
    private String wishSymbol;

    @Autowired
    private UserSiteBalanceRepository userSiteBalanceRepository;

    @Autowired
    private EventPublisher eventPublisher;

    private Map<String, CryptoCurrency> symbols = new HashMap<>();

    @PostConstruct
    private void init() {
        symbols.put(binanceSymbol, CryptoCurrency.BBNB);
        symbols.put(wishSymbol, CryptoCurrency.BWISH);

        log.info("Target address: {}, token symbols: {}.", targetAddress, String.join(", ", symbols.keySet()));
    }

    @EventListener
    public void newBlockEvent(final NewBlockEvent newBlockEvent) {
        if (newBlockEvent.getNetworkType() != NetworkType.BINANCE_MAINNET) {
            return;
        }

        newBlockEvent.getTransactionsByAddress()
                .entrySet()
                .stream()
                .filter(entry -> entry.getKey().equalsIgnoreCase(targetAddress))
                .map(Map.Entry::getValue)
                .flatMap(Collection::stream)
                .forEach(transaction -> transaction.getOutputs()
                        .stream()
                        .map(output -> ((WrapperOutputBinance) output))
                        .filter(output -> targetAddress.equalsIgnoreCase(output.getAddress()))
                        .filter(output -> symbols.keySet().contains(output.getSymbol()))
                        .forEach(output -> {
                            UserSiteBalance userSiteBalance = userSiteBalanceRepository.findByMemo(output.getMemo());
                            if (userSiteBalance == null) {
                                log.warn("Transfer received, but with wrong memo {} from {}.", output.getMemo(), output.getFrom());
                                return;
                            }

                            eventPublisher.publish(new UserPaymentEvent(
                                    newBlockEvent.getNetworkType(),
                                    transaction,
                                    output.getValue(),
                                    symbols.get(output.getSymbol()),
                                    true,
                                    userSiteBalance
                            ));
                        })
                );
    }
}
