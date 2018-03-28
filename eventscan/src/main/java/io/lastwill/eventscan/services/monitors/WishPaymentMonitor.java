package io.lastwill.eventscan.services.monitors;

import io.lastwill.eventscan.events.UserPaymentEvent;
import io.lastwill.eventscan.events.contract.erc20.TransferEvent;
import io.lastwill.eventscan.model.CryptoCurrency;
import io.lastwill.eventscan.model.UserProfile;
import io.lastwill.eventscan.repositories.UserProfileRepository;
import io.lastwill.eventscan.services.EventParser;
import io.lastwill.eventscan.services.TransactionProvider;
import io.lastwill.eventscan.services.builders.erc20.TransferEventBuilder;
import io.mywish.scanner.model.NetworkType;
import io.mywish.scanner.services.EventPublisher;
import io.mywish.scanner.model.NewBlockEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import org.web3j.protocol.core.methods.response.Transaction;

import javax.annotation.PostConstruct;
import java.math.BigInteger;
import java.util.List;
import java.util.Set;

@Slf4j
@Component
public class WishPaymentMonitor {
    @Autowired
    private UserProfileRepository userProfileRepository;
    @Autowired
    private EventPublisher eventPublisher;
    @Autowired
    private TransactionProvider transactionProvider;
    @Autowired
    private EventParser eventParser;
    @Autowired
    private TransferEventBuilder transferEventBuilder;

    @Value("${io.lastwill.eventscan.contract.token-address}")
    private String tokenAddress;

    @PostConstruct
    protected void init() {
        if (tokenAddress != null) {
            tokenAddress = tokenAddress.toLowerCase();
        }
        log.info("Token address: {}.", tokenAddress);
    }

    @EventListener
    public void onNewBlock(final NewBlockEvent newBlockEvent) {
        // wish only in mainnet works
        if (newBlockEvent.getNetworkType() != NetworkType.ETHEREUM_MAINNET) {
            return;
        }
        Set<String> addresses = newBlockEvent.getTransactionsByAddress().keySet();
        if (addresses.isEmpty()) {
            return;
        }

        if (!addresses.contains(tokenAddress)) {
            return;
        }

        List<Transaction> transactions = newBlockEvent.getTransactionsByAddress().get(tokenAddress);
        for (final Transaction transaction : transactions) {
            if (!tokenAddress.equalsIgnoreCase(transaction.getTo())) {
                continue;
            }
            transactionProvider.getTransactionReceiptAsync(transaction.getHash())
                    .thenAccept(transactionReceipt -> eventParser.parseEvents(transactionReceipt, transferEventBuilder.getEventSignature())
                            .stream()
                            .filter(event -> event instanceof TransferEvent)
                            .map(event -> (TransferEvent) event)
                            .forEach(eventValue -> {
                                try {
                                    String transferTo = eventValue.getTo();
                                    BigInteger amount = eventValue.getTokens();

                                    UserProfile userProfile = userProfileRepository.findByInternalAddress(transferTo);
                                    if (userProfile == null) {
                                        return;
                                    }
                                    eventPublisher.publish(new UserPaymentEvent(
                                            newBlockEvent.getNetworkType(),
                                            transaction,
                                            amount,
                                            CryptoCurrency.WISH,
                                            true,
                                            userProfile));
                                }
                                catch (Exception e) {
                                    log.warn("Error on parsing ERC20 Transfer event.", e);
                                }
                            }));

        }
    }
}
