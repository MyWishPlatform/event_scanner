package io.lastwill.eventscan.services.monitors.payments;

import io.lastwill.eventscan.events.model.UserPaymentEvent;
import io.lastwill.eventscan.events.model.contract.erc20.TransferEvent;
import io.lastwill.eventscan.model.CryptoCurrency;
import io.lastwill.eventscan.model.NetworkType;
import io.lastwill.eventscan.model.UserSiteBalance;
import io.lastwill.eventscan.repositories.UserProfileRepository;
import io.lastwill.eventscan.repositories.UserSiteBalanceRepository;
import io.lastwill.eventscan.services.TransactionProvider;
import io.mywish.blockchain.WrapperTransaction;
import io.mywish.scanner.model.NewBlockEvent;
import io.mywish.scanner.services.EventPublisher;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.math.BigInteger;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Slf4j
@Component
public class ERC20PaymentMonitor {
    @Autowired
    private UserSiteBalanceRepository  userSiteBalanceRepository;
    @Autowired
    private EventPublisher eventPublisher;
    @Autowired
    private TransactionProvider transactionProvider;

    @Value("${io.lastwill.eventscan.contract.token-address}")
    private String tokenAddressWish;

    @Value("${io.lastwill.eventscan.contract.token-address}")
    private String tokenAddressBnb;

    private final Map<String, CryptoCurrency> addressToCurrency = new HashMap<>();

    @PostConstruct
    protected void init() {
        addressToCurrency.put(tokenAddressWish.toLowerCase(), CryptoCurrency.WISH);
        addressToCurrency.put(tokenAddressBnb.toLowerCase(), CryptoCurrency.BNB);

        addressToCurrency.forEach(
                (address, cryptoCurrency) -> log.info("Payment waiting {} - {}.", cryptoCurrency, address)
        );
    }

    @EventListener
    public void onNewBlock(final NewBlockEvent event) {
        // wish only in mainnet works
        if (event.getNetworkType() != NetworkType.ETHEREUM_MAINNET) {
            return;
        }
        Set<String> addresses = event.getTransactionsByAddress().keySet();
        if (addresses.isEmpty()) {
            return;
        }

        addressToCurrency.forEach((tokenAddress, currency) -> {
            if (!addresses.contains(tokenAddress)) {
                return;
            }

            List<WrapperTransaction> transactions = event.getTransactionsByAddress().get(tokenAddress);

            handle(tokenAddress, transactions, currency, event.getNetworkType());
        });

    }

    private void handle(final String tokenAddress, final List<WrapperTransaction> transactions, final CryptoCurrency currency, NetworkType networkType) {
        for (final WrapperTransaction transaction : transactions) {
            if (!tokenAddress.equalsIgnoreCase(transaction.getOutputs().get(0).getAddress())) {
                continue;
            }
            transactionProvider.getTransactionReceiptAsync(networkType, transaction)
                    .thenAccept(transactionReceipt -> transactionReceipt.getLogs()
                            .stream()
                            .filter(event -> event instanceof TransferEvent)
                            .map(event -> (TransferEvent) event)
                            .forEach(eventValue -> {
                                String transferTo = eventValue.getTo();
                                BigInteger amount = eventValue.getTokens();

                                UserSiteBalance userSiteBalance = userSiteBalanceRepository.findByEthAddress(transferTo);
                                if (userSiteBalance == null) {
                                    return;
                                }
                                eventPublisher.publish(new UserPaymentEvent(
                                        networkType,
                                        transaction,
                                        amount,
                                        currency,
                                        true,
                                        userSiteBalance));
                            }))
                    .exceptionally(throwable -> {
                        log.error("Error on getting receipt for handling WISH payment.", throwable);
                        return null;
                    });

        }

    }
}
