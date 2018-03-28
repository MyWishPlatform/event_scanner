package io.lastwill.eventscan.services.monitors;

import io.lastwill.eventscan.events.UserPaymentEvent;
import io.lastwill.eventscan.helpers.TransactionHelper;
import io.lastwill.eventscan.model.CryptoCurrency;
import io.lastwill.eventscan.model.UserProfile;
import io.lastwill.eventscan.repositories.UserProfileRepository;
import io.lastwill.eventscan.services.TransactionProvider;
import io.mywish.scanner.services.EventPublisher;
import io.mywish.scanner.model.NewBlockEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import org.web3j.protocol.core.methods.response.Transaction;

import java.math.BigInteger;
import java.util.List;
import java.util.Set;

@Slf4j
@Component
public class EthPaymentMonitor {
    @Autowired
    private EventPublisher eventPublisher;

    @Autowired
    private UserProfileRepository userProfileRepository;

    @Autowired
    private TransactionProvider transactionProvider;

    @EventListener
    public void onNewBlockEvent(NewBlockEvent event) {
        Set<String> addresses = event.getTransactionsByAddress().keySet();
        if (addresses.isEmpty()) {
            return;
        }

        List<UserProfile> userProfiles = userProfileRepository.findByAddressesList(addresses);
        for (UserProfile userProfile : userProfiles) {
            final List<Transaction> transactions = event.getTransactionsByAddress().get(
                    userProfile.getInternalAddress()
            );

            transactions.forEach(transaction -> {
                if (!userProfile.getInternalAddress().equalsIgnoreCase(transaction.getTo())) {
                    log.debug("Found transaction out from internal address. Skip it.");
                    return;
                }
                transactionProvider.getTransactionReceiptAsync(transaction.getHash())
                        .thenAccept(receipt -> {
                            eventPublisher.publish(new UserPaymentEvent(,
                                    transaction,
                                    getAmountFor(userProfile.getInternalAddress(), transaction),
                                    CryptoCurrency.ETH,
                                    TransactionHelper.isSuccess(receipt),
                                    userProfile));
                        });
            });
        }
    }

    private BigInteger getAmountFor(final String address, final Transaction transaction) {
        BigInteger result = BigInteger.ZERO;
        if (address.equalsIgnoreCase(transaction.getFrom())) {
            result = result.subtract(transaction.getValue());
        }
        if (address.equalsIgnoreCase(transaction.getTo())) {
            result = result.add(transaction.getValue());
        }
        return result;
    }

}
