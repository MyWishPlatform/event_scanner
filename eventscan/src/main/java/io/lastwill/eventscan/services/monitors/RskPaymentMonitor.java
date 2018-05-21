package io.lastwill.eventscan.services.monitors;

import io.lastwill.eventscan.events.FGWBalanceChangedEvent;
import io.lastwill.eventscan.model.CryptoCurrency;
import io.lastwill.eventscan.services.BalanceProvider;
import io.lastwill.eventscan.services.NetworkProvider;
import io.lastwill.eventscan.model.NetworkType;
import io.mywish.wrapper.WrapperTransaction;
import io.mywish.scanner.model.NewBlockEvent;
import io.mywish.scanner.services.EventPublisher;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.math.BigInteger;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@Component
public class RskPaymentMonitor {
    @Autowired
    private NetworkProvider networkProvider;

    @Autowired
    private BalanceProvider balanceProvider;

    @Autowired
    private EventPublisher eventPublisher;

    @Value("${io.lastwill.eventscan.rsk.rsk-federation-gatewat-testnet-address:#{null}}")
    private String mainAddressTestnet;

    @Value("${io.lastwill.eventscan.rsk.rsk-federation-gatewat-mainnet-address:#{null}}")
    private String mainAddressMainnet;

    private final Map<NetworkType, String> addressByNet = new HashMap<>();

    private final ConcurrentHashMap<String, BigInteger> latestBalance = new ConcurrentHashMap<>();

    @PostConstruct
    protected void init() {
        if (mainAddressMainnet != null) {
            if (networkProvider.getAvailableNetworkTypes().contains(NetworkType.RSK_MAINNET)) {
                addressByNet.put(NetworkType.RSK_MAINNET, mainAddressMainnet.toLowerCase());
            }
            else {
                log.warn("There is mainnet address configured, but not mainnet Web3.");
            }
        }
        if (mainAddressTestnet != null) {
            if (networkProvider.getAvailableNetworkTypes().contains(NetworkType.RSK_TESTNET)) {
                addressByNet.put(NetworkType.RSK_TESTNET, mainAddressTestnet.toLowerCase());
            }
            else {
                log.warn("There is testnet address configured, but not mainnet Web3.");
            }
        }
    }

    @EventListener
    private void newBlockHandler(final NewBlockEvent event) {
        if (!addressByNet.containsKey(event.getNetworkType())) {
            return;
        }

        final String lookingAddress = addressByNet.get(event.getNetworkType());
        List<WrapperTransaction> transactions = event.getTransactionsByAddress().get(lookingAddress);
        if (transactions == null || transactions.isEmpty()) {
            return;
        }

        latestBalance.computeIfAbsent(lookingAddress, address -> {
            try {
                return balanceProvider.getBalance(event.getNetworkType(), address, event.getBlock().getNumber());
            }
            catch (IOException e) {
                log.warn("Error on getting first time balance in {} for FGW address {}.", event.getNetworkType(), lookingAddress, e);
                return null;
            }
        });

        final BigInteger balance = latestBalance.get(lookingAddress);
        if (balance == null) {
            log.warn("Skip block {}, because there is no initial balance for address {} in network {}.", event.getBlock().getNumber(), lookingAddress, event.getNetworkType());
            return;
        }

        balanceProvider.getBalanceAsync(event.getNetworkType(), lookingAddress, event.getBlock().getNumber())
                .thenAccept(newBalance -> {
                    final BigInteger delta = balance.subtract(newBalance);
                    if (delta.compareTo(BigInteger.ZERO) == 0) {
                        return;
                    }
                    eventPublisher.publish(new FGWBalanceChangedEvent(
                            event.getNetworkType(),
                            lookingAddress,
                            newBalance,
                            delta,
                            CryptoCurrency.RSK,
                            event.getBlock().getNumber(),
                            true
                    ));
                })
                .exceptionally(throwable -> {
                    log.warn("Error on getting balance for FGW address {} in {}.", lookingAddress, event.getNetworkType(), throwable);
                    return null;
                });
    }
}
