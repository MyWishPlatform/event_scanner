package io.lastwill.eventscan.services.monitors.payments;

import io.lastwill.eventscan.events.model.UserPaymentEvent;
import io.lastwill.eventscan.events.model.contract.eos.EosTransferEvent;
import io.lastwill.eventscan.model.CryptoCurrency;
import io.lastwill.eventscan.model.NetworkType;
import io.lastwill.eventscan.model.UserProfile;
import io.lastwill.eventscan.model.UserSiteBalance;
import io.lastwill.eventscan.repositories.UserProfileRepository;
import io.lastwill.eventscan.repositories.UserSiteBalanceRepository;
import io.lastwill.eventscan.services.TransactionProvider;
import io.mywish.blockchain.WrapperOutput;
import io.mywish.blockchain.WrapperTransaction;
import io.mywish.blockchain.WrapperTransactionReceipt;
import io.mywish.scanner.model.NewBlockEvent;
import io.mywish.scanner.services.EventPublisher;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.nio.charset.StandardCharsets;
import java.util.List;

@Slf4j
@Component
public class EosPaymentMonitor {
    @Value("${io.lastwill.eventscan.eos.token-contract}")
    private String tokenContract;
    @Value("${io.lastwill.eventscan.eos.target-address}")
    private String targetAddress;
    @Value("${io.lastwill.eventscan.eos.token-symbol}")
    private String tokeSymbol;

    @Autowired
    private UserSiteBalanceRepository userSiteBalanceRepository;

    @Autowired
    private TransactionProvider transactionProvider;

    @Autowired
    private EventPublisher eventPublisher;

    @PostConstruct
    private void init() {
        log.info("Contract {}, target address {}, token symbol {}.", tokenContract, targetAddress, tokeSymbol);
    }

    @EventListener
    public void newBlockEvent(final NewBlockEvent newBlockEvent) {
        if (newBlockEvent.getNetworkType() != NetworkType.EOS_MAINNET) {
            return;
        }

        final List<WrapperTransaction> transactions = newBlockEvent.getTransactionsByAddress().get(tokenContract);
        if (transactions == null || transactions.isEmpty()) {
            return;
        }

        for (WrapperTransaction transaction: transactions) {
            for (WrapperOutput output : transaction.getOutputs()) {
                if (!output.getAddress().equalsIgnoreCase(tokenContract)) {
                    continue;
                }

                final WrapperTransactionReceipt receipt;
                try {
                    receipt = transactionProvider.getTransactionReceipt(
                            newBlockEvent.getNetworkType(),
                            transaction
                    );
                }
                catch (Exception e) {
                    log.error("Error on getting receipt tx {}.", transaction, e);
                    continue;
                }

                receipt.getLogs()
                        .stream()
                        .filter(event -> event instanceof EosTransferEvent)
                        .map(event -> (EosTransferEvent) event)
                        .filter(eosTransferEvent -> tokenContract.equalsIgnoreCase(eosTransferEvent.getAddress()))
                        .filter(eosTransferEvent -> targetAddress.equalsIgnoreCase(eosTransferEvent.getTo()))
                        .filter(eosTransferEvent -> tokeSymbol.equalsIgnoreCase(eosTransferEvent.getSymbol()))
                        .forEach(transferEvent -> {
                            String memo = new String(transferEvent.getData(), StandardCharsets.US_ASCII);
                            UserSiteBalance userSiteBalance = userSiteBalanceRepository.findByMemo(memo);
                            if (userSiteBalance == null) {
                                String from = transaction.isSingleInput() ? transaction.getSingleInputAddress() : "?unknown";
                                log.warn("Transfer received, but with wrong memo {} from {}.", memo, from);
                                return;
                            }

                            eventPublisher.publish(new UserPaymentEvent(
                                    newBlockEvent.getNetworkType(),
                                    transaction,
                                    transferEvent.getTokens(),
                                    CryptoCurrency.EOS,
                                    receipt.isSuccess(),
                                    userSiteBalance
                            ));
                        });
            }
        }
    }
}
