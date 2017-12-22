package io.lastwill.eventscan.services.monitors;

import io.lastwill.eventscan.events.UserPaymentEvent;
import io.lastwill.eventscan.model.CryptoCurrency;
import io.lastwill.eventscan.model.UserProfile;
import io.lastwill.eventscan.repositories.UserProfileRepository;
import io.lastwill.eventscan.services.EventParser;
import io.lastwill.eventscan.services.TransactionProvider;
import io.mywish.scanner.EventPublisher;
import io.mywish.scanner.NewBlockEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import org.web3j.abi.datatypes.Address;
import org.web3j.abi.datatypes.Uint;
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
                    .thenAccept(transactionReceipt -> eventParser.parseEvents(transactionReceipt, eventParser.TransferERC20)
                            .forEach(eventValue -> {
                                try {
                                    Address address = (Address) eventValue
                                            .getIndexedValues()
                                            .get(1)
                                            .getValue();

                                    Uint amount = (Uint) eventValue
                                            .getNonIndexedValues()
                                            .get(0)
                                            .getValue();

                                    String strAddress = address.getValue().toLowerCase();
                                    BigInteger intAmount = amount.getValue();

                                    UserProfile userProfile = userProfileRepository.findByInternalAddress(strAddress);
                                    eventPublisher.publish(new UserPaymentEvent(
                                            transaction,
                                            intAmount,
                                            CryptoCurrency.WISH,
                                            true,
                                            userProfile
                                    ));
                                }
                                catch (Exception e) {
                                    log.warn("Error on parsing ERC20 Transfer event.", e);
                                }
                            }));

        }
    }
}
