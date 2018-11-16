package io.lastwill.eventscan.services.handlers;

import io.lastwill.eventscan.events.model.PendingTransactionAddedEvent;
import io.lastwill.eventscan.events.model.PendingTransactionRemovedEvent;
import io.lastwill.eventscan.events.model.contract.AirdropEvent;
import io.lastwill.eventscan.messages.AirdropNotify;
import io.lastwill.eventscan.messages.PaymentStatus;
import io.lastwill.eventscan.model.*;
import io.lastwill.eventscan.repositories.ContractRepository;
import io.lastwill.eventscan.repositories.ProductRepository;
import io.lastwill.eventscan.services.ExternalNotifier;
import io.lastwill.eventscan.services.TransactionProvider;
import io.mywish.blockchain.WrapperTransaction;
import io.mywish.blockchain.WrapperTransactionReceipt;
import io.mywish.scanner.model.NewBlockEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Slf4j
@Component
public class EosAirdropEventHandler {
    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private ContractRepository contractRepository;

    @Autowired
    private TransactionProvider transactionProvider;

    @Autowired
    private ExternalNotifier externalNotifier;

    @Value("${io.lastwill.eventscan.eos.airdrop-contract}")
    private String airdropAccount;

    @EventListener
    public void onNewBlock(NewBlockEvent event) {
        if (event.getNetworkType().getNetworkProviderType() != NetworkProviderType.EOS) {
            return;
        }

        List<WrapperTransaction> transactions = event.getTransactionsByAddress().get(airdropAccount);
        if (transactions == null) {
            return;
        }

        for (WrapperTransaction transaction : transactions) {
            if (transaction.getOutputs().stream().noneMatch(output -> airdropAccount.equalsIgnoreCase(output.getAddress()))) {
                continue;
            }

            WrapperTransactionReceipt transactionReceipt;
            try {
                transactionReceipt = transactionProvider.getTransactionReceipt(event.getNetworkType(), transaction);
            }
            catch (Exception e) {
                log.warn("Error on getting tx receipt.", e);
                continue;
            }

            handleTransaction(transactionReceipt, event.getNetworkType(), PaymentStatus.COMMITTED);
        }
    }

    @EventListener
    public void onNewPendingTransaction(PendingTransactionAddedEvent event) {
        if (event.getNetworkType().getNetworkProviderType() != NetworkProviderType.EOS) {
            return;
        }

        WrapperTransactionReceipt transactionReceipt;
        try {
            transactionReceipt = transactionProvider.getTransactionReceipt(event.getNetworkType(), event.getTransaction());
        }
        catch (Exception e) {
            log.warn("Error on getting tx receipt.", e);
            return;
        }

        handleTransaction(transactionReceipt, event.getNetworkType(), PaymentStatus.PENDING);
    }

    @EventListener
    public void onNewRejectedTransaction(PendingTransactionRemovedEvent event) {
        if (event.getNetworkType().getNetworkProviderType() != NetworkProviderType.EOS) {
            return;
        }
        if (event.getReason() != PendingTransactionRemovedEvent.Reason.TIMEOUT) {
            return;
        }

        WrapperTransactionReceipt transactionReceipt;
        try {
            transactionReceipt = transactionProvider.getTransactionReceipt(event.getNetworkType(), event.getTransaction());
        }
        catch (Exception e) {
            log.warn("Error on getting tx receipt.", e);
            return;
        }

        handleTransaction(transactionReceipt, event.getNetworkType(), PaymentStatus.REJECTED);
    }

    private void handleTransaction(final WrapperTransactionReceipt transactionReceipt, final NetworkType networkType, final PaymentStatus paymentStatus) {
            transactionReceipt
                    .getLogs()
                    .stream()
                    .filter(contractEvent -> contractEvent instanceof AirdropEvent)
                    .map(contractEvent -> (AirdropEvent) contractEvent)
                    .forEach(airdropEvent -> {
                        if (airdropEvent.getAddresses().size() != airdropEvent.getValues().size()) {
                            log.warn("Airdrop event wrong format: address array is not synced with values.\n{}", airdropEvent);
                            return;
                        }

                        List<AirdropEntry> airdropEntries = IntStream.range(0, airdropEvent.getAddresses().size())
                                .mapToObj(index -> new AirdropEntry(airdropEvent.getAddresses().get(index), airdropEvent.getValues().get(index)))
                                .collect(Collectors.toList());

                        ProductAirdropEos productAirdropEos = (ProductAirdropEos) productRepository.findOne((int) airdropEvent.getPk());

                        if (productAirdropEos == null) {
                            log.warn("There were no one airdrop product found for event {}.", airdropEvent);
                            return;
                        }

                        List<Contract> contracts = contractRepository.findByProduct(productAirdropEos);
                        if (contracts.isEmpty()) {
                            log.warn("There were no one contract found for airdrop {}.", productAirdropEos);
                            return;
                        }

                        contracts.forEach(contract -> {
                            externalNotifier.send(
                                    networkType,
                                    new AirdropNotify(
                                            contract.getId(),
                                            paymentStatus,
                                            transactionReceipt.getTransactionHash(),
                                            airdropEntries
                                    )
                            );
                        });;
                    });
    }
}
