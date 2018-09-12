package io.lastwill.eventscan.services.monitors.payments;

import io.lastwill.eventscan.events.model.UserPaymentEvent;
import io.lastwill.eventscan.events.model.contract.eos.EosTransferEvent;
import io.lastwill.eventscan.events.model.contract.erc223.Erc223TransferEvent;
import io.lastwill.eventscan.model.CryptoCurrency;
import io.lastwill.eventscan.model.NetworkType;
import io.lastwill.eventscan.model.UserProfile;
import io.lastwill.eventscan.repositories.UserProfileRepository;
import io.lastwill.eventscan.services.TransactionProvider;
import io.mywish.scanner.model.NewBlockEvent;
import io.mywish.scanner.services.EventPublisher;
import io.mywish.wrapper.WrapperOutput;
import io.mywish.wrapper.WrapperTransaction;
import io.mywish.wrapper.WrapperTransactionReceipt;
import io.mywish.wrapper.model.output.WrapperOutputEos;
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
public class EosishPaymentMonitor {
    @Value("${io.lastwill.eventscan.eosish.token-contract}")
    private String tokenContract;
    @Value("${io.lastwill.eventscan.eos.token-action}")
    private String tokenAction;
    @Value("${io.lastwill.eventscan.eos.target-address}")
    private String targetAddress;
    @Value("${io.lastwill.eventscan.eosish.token-symbol}")
    private String tokeSymbol;

    @Autowired
    private TransactionProvider transactionProvider;

    @Autowired
    private UserProfileRepository userProfileRepository;

    @Autowired
    private EventPublisher eventPublisher;

    @PostConstruct
    private void init() {
        log.info("Contract={}, symbol={}, action={}, target address={}.", tokenContract, tokeSymbol, tokenAction, targetAddress);
    }

    @EventListener
    public void newBlockEvent(final NewBlockEvent newBlockEvent) {
        if (newBlockEvent.getNetworkType() != NetworkType.EOS_MAINNET) {
            return;
        }

        List<WrapperTransaction> transactions = newBlockEvent.getTransactionsByAddress().get(tokenContract);
        if (transactions == null || transactions.isEmpty()) {
            return;
        }

        for (WrapperTransaction transaction: transactions) {
            for (WrapperOutput output : transaction.getOutputs()) {
                if (!output.getAddress().equalsIgnoreCase(tokenContract)) {
                    continue;
                }
                WrapperOutputEos outputEos = (WrapperOutputEos) output;
                if (!outputEos.getName().equalsIgnoreCase(tokenAction)) {
                    continue;
                }

                WrapperTransactionReceipt receipt;
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
                        .forEach(transferEvent -> {
                            if (!targetAddress.equalsIgnoreCase(transferEvent.getTo())) {
                                return;
                            }

                            if (!tokeSymbol.equalsIgnoreCase(transferEvent.getSymbol())) {
                                return;
                            }

                            String memo = new String(transferEvent.getData(), StandardCharsets.US_ASCII);
                            UserProfile userProfile = userProfileRepository.findByMemo(memo);
                            if (userProfile == null) {
                                String from = transaction.isSingleInput() ? transaction.getSingleInputAddress() : "?unknown";
                                log.warn("Transfer received, but with wrong memo {} from {}.", memo, from);
                                return;
                            }

                            eventPublisher.publish(new UserPaymentEvent(
                                    newBlockEvent.getNetworkType(),
                                    transaction,
                                    transferEvent.getTokens(),
                                    CryptoCurrency.EOSISH,
                                    receipt.isSuccess(),
                                    userProfile
                            ));
                        });
            }
        }
    }
}
