package io.lastwill.eventscan.services.monitors.payments;

import io.lastwill.eventscan.events.model.UserPaymentEvent;
import io.lastwill.eventscan.events.model.contract.erc20.TransferEvent;
import io.lastwill.eventscan.model.CryptoCurrency;
import io.lastwill.eventscan.model.NetworkType;
import io.lastwill.eventscan.model.UserSiteBalance;
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

import java.math.BigInteger;
import java.util.List;
import java.util.Set;

@Slf4j
//@Component
public class TronishPaymentMonitor {
    /*
    @Autowired
    private UserSiteBalanceRepository userSiteBalanceRepository;
    @Autowired
    private EventPublisher eventPublisher;
    @Autowired
    private TransactionProvider transactionProvider;

    @Value("${io.lastwill.eventscan.tronish.token-contract}")
    private String tokenAddressTronish;

    @EventListener
    public void onNewBlock(final NewBlockEvent event) {
        // tronish only in mainnet works
        if (event.getNetworkType() != NetworkType.TRON_MAINNET) {
            return;
        }
        Set<String> addresses = event.getTransactionsByAddress().keySet();
        if (addresses.isEmpty()) {
            return;
        }

        if (!addresses.contains(tokenAddressTronish)) {
            return;
        }

        List<WrapperTransaction> transactions = event.getTransactionsByAddress().get(tokenAddressTronish);
        handle(tokenAddressTronish, transactions, event.getNetworkType());
    }

    private void handle(final String tokenAddress, final List<WrapperTransaction> transactions, NetworkType networkType) {
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
                                String transferTo = eventValue.getTo().replaceFirst("^0x", "41");
                                BigInteger amount = eventValue.getTokens();

                                UserSiteBalance userSiteBalance = userSiteBalanceRepository.findByTronAddress(transferTo);
                                if (userSiteBalance == null) {
                                    return;
                                }
                                eventPublisher.publish(new UserPaymentEvent(
                                        networkType,
                                        transaction,
                                        amount,
                                        CryptoCurrency.TRONISH,
                                        true,
                                        userSiteBalance));
                            }))
                    .exceptionally(throwable -> {
                        log.error("Error on getting receipt for handling TRONISH payment.", throwable);
                        return null;
                    });

        }

    }
    */
}
