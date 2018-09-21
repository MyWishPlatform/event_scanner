package io.lastwill.eventscan.services.monitors;

import io.lastwill.eventscan.events.model.ContractTransactionFailedEvent;
import io.lastwill.eventscan.events.model.contract.erc20.TransferEvent;
import io.lastwill.eventscan.messages.FinalizedNotify;
import io.lastwill.eventscan.messages.PaymentStatus;
import io.lastwill.eventscan.messages.TokensAddedNotify;
import io.lastwill.eventscan.model.Contract;
import io.lastwill.eventscan.model.NetworkType;
import io.lastwill.eventscan.model.ProductInvestmentPool;
import io.lastwill.eventscan.repositories.ContractRepository;
import io.lastwill.eventscan.repositories.ProductRepository;
import io.lastwill.eventscan.services.ExternalNotifier;
import io.lastwill.eventscan.services.TransactionProvider;
import io.mywish.scanner.model.NewBlockEvent;
import io.mywish.blockchain.WrapperOutput;
import io.mywish.blockchain.WrapperTransaction;
import io.mywish.blockchain.WrapperTransactionReceipt;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import java.nio.ByteBuffer;
import java.util.List;

@Slf4j
@Component
public class InvestmentPoolMonitor {
    private static final int FINALIZE_METHOD_ID = 0x4bb278f3;
    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private TransactionProvider transactionProvider;

    @Autowired
    private ExternalNotifier externalNotifier;

    @Autowired
    private ContractRepository contractRepository;

    @EventListener
    protected void onNewBlock(NewBlockEvent event) {
        if (event.getTransactionsByAddress().isEmpty()) {
            return;
        }
        if (event.getNetworkType() != NetworkType.ETHEREUM_MAINNET
                && event.getNetworkType() != NetworkType.ETHEREUM_ROPSTEN) {
            return;
        }
        List<ProductInvestmentPool> products = productRepository.findIPoolByTokenAddress(
                event.getTransactionsByAddress().keySet(),
                event.getNetworkType()
        );
        if (products.isEmpty()) {
            return;
        }

        for (final ProductInvestmentPool product : products) {
            String tokenAddress = product.getTokenAddress();
            if (tokenAddress == null) {
                log.error("IPool {} has empty token address.", product.getId(), new IllegalStateException());
                continue;
            }

            List<Contract> contracts = contractRepository.findByProduct(product);
            if (contracts.isEmpty()) {
                log.error("IPool {} does not have contract!", product.getId());
                continue;
            }

            if (contracts.size() > 1) {
                log.warn("IPool {} has more then one contract. The first will be taken.", product.getId());
            }
            Contract contract = contracts.get(0);

            for (WrapperTransaction transaction : event.getTransactionsByAddress().get(tokenAddress.toLowerCase())) {
                // skip empty output, or if output is not token address
                if (!transaction.isSingleOutput() ||
                        !transaction.getSingleOutputAddress().equalsIgnoreCase(tokenAddress)) {
                    continue;
                }

                WrapperTransactionReceipt transactionReceipt;
                try {
                    transactionReceipt = transactionProvider.getTransactionReceipt(
                            event.getNetworkType(),
                            transaction
                    );
                }
                catch (Exception e) {
                    log.error("Error on getting tx receipt. IPool was {}, tx was {}.", product.getId(), transaction.getHash(), e);
                    continue;
                }

                transactionReceipt.getLogs()
                        .stream()
                        .filter(contractEvent -> contractEvent instanceof TransferEvent)
                        .map(contractEvent -> (TransferEvent) contractEvent)
                        .filter(transferEvent -> transferEvent.getTo().equalsIgnoreCase(contract.getAddress()))
                        .forEach(transferEvent -> externalNotifier.send(event.getNetworkType(),
                                new TokensAddedNotify(
                                        product.getId(),
                                        transaction.getHash(),
                                        tokenAddress,
                                        transferEvent.getTokens()
                                )));

            }
        }
    }

    @EventListener
    protected void handleContractFailed(ContractTransactionFailedEvent event) {
        if (!(event.getContract().getProduct() instanceof ProductInvestmentPool)) {
            return;
        }
        event.getTransaction()
                .getOutputs()
                .stream()
                .filter(wrapperOutput -> wrapperOutput.getAddress().equalsIgnoreCase(event.getContract().getAddress()))
                .filter(InvestmentPoolMonitor::isFinalizeMethod)
                .forEach(wrapperOutput -> {
                    externalNotifier.send(
                            event.getNetworkType(),
                            new FinalizedNotify(
                                    event.getContract().getId(),
                                    PaymentStatus.REJECTED,
                                    event.getTransaction().getHash()
                            )
                    );
                });
    }

    private static boolean isFinalizeMethod(WrapperOutput wrapperOutput) {
        if (wrapperOutput.getRawOutputScript().length < 4) {
            return false;
        }
        ByteBuffer buffer = ByteBuffer.wrap(wrapperOutput.getRawOutputScript());
        int methodId = buffer.getInt();
        return FINALIZE_METHOD_ID == methodId;
    }
}
