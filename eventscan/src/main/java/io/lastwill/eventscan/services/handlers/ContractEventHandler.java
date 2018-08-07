package io.lastwill.eventscan.services.handlers;

import io.lastwill.eventscan.events.model.ContractEventsEvent;
import io.lastwill.eventscan.events.model.CreateTokenEvent;
import io.lastwill.eventscan.events.model.contract.*;
import io.lastwill.eventscan.events.model.contract.crowdsale.FinalizedEvent;
import io.lastwill.eventscan.events.model.contract.crowdsale.TimesChangedEvent;
import io.lastwill.eventscan.events.model.contract.crowdsale.WhitelistedAddressAddedEvent;
import io.lastwill.eventscan.events.model.contract.crowdsale.WhitelistedAddressRemovedEvent;
import io.lastwill.eventscan.events.model.contract.erc20.TransferEvent;
import io.lastwill.eventscan.events.model.contract.investmentPool.*;
import io.lastwill.eventscan.messages.*;
import io.lastwill.eventscan.model.AirdropEntry;
import io.lastwill.eventscan.model.ProductAirdrop;
import io.lastwill.eventscan.model.ProductInvestmentPool;
import io.lastwill.eventscan.repositories.ProductRepository;
import io.lastwill.eventscan.services.BalanceProvider;
import io.lastwill.eventscan.services.ExternalNotifier;
import io.lastwill.eventscan.services.handlers.events.TransferOwnershipHandler;
import io.mywish.wrapper.ContractEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Component
@ConditionalOnBean(ExternalNotifier.class)
public class ContractEventHandler {
    @Autowired
    private ExternalNotifier externalNotifier;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private BalanceProvider balanceProvider;

    @Autowired
    private TransferOwnershipHandler transferOwnershipHandler;

    @EventListener
    private void eventsHandler(final ContractEventsEvent event) {
        // catch airdrop events
        if (event.getContract().getProduct() instanceof ProductAirdrop) {
            List<AirdropEntry> airdropAddresses = event.getEvents()
                    .stream()
                    .filter(contractEvent -> contractEvent instanceof TransferEvent)
                    .map(contractEvent -> (TransferEvent) contractEvent)
                    .map(transferEvent -> new AirdropEntry(transferEvent.getTo(), transferEvent.getTokens()))
                    .collect(Collectors.toList());

            externalNotifier.send(event.getNetworkType(),
                    new AirdropNotify(
                            event.getContract().getId(),
                            PaymentStatus.COMMITTED,
                            event.getTransaction().getHash(),
                            airdropAddresses
                    )
            );
            return;
        }
        for (ContractEvent contractEvent : event.getEvents()) {
            // skip event if event.address != contract.address (it might be when internal transaction occurs)
            if (!contractEvent.getAddress().equalsIgnoreCase(event.getContract().getAddress())) {
                log.warn("There is skipped internal transaction event to address {} with name {}.", contractEvent.getAddress(), contractEvent.getName());
                continue;
            }

            if (contractEvent instanceof CreateTokenEvent) {
                CreateTokenEvent createTokenEvent = (CreateTokenEvent)contractEvent;
                externalNotifier.send(event.getNetworkType(),
                        new CreateNotify(event.getContract().getId(),
                                event.getTransaction().getHash(),
                                createTokenEvent.getIssuer(),
                                createTokenEvent.getSupply()));
            }
            else if (contractEvent instanceof CheckedEvent) {
                externalNotifier.send(event.getNetworkType(),
                        new CheckedNotify(event.getContract().getId(), event.getTransaction().getHash()));
            }
            else if (contractEvent instanceof NeedRepeatCheckEvent) {
                externalNotifier.send(event.getNetworkType(),
                        new RepeatCheckNotify(event.getContract().getId(), event.getTransaction().getHash()));
            }
            else if (contractEvent instanceof KilledEvent) {
                externalNotifier.send(event.getNetworkType(),
                        new ContractKilledNotify(event.getContract().getId(), event.getTransaction().getHash()));
            }
            else if (contractEvent instanceof TriggeredEvent) {
                externalNotifier.send(event.getNetworkType(),
                        new ContractTriggeredNotify(event.getContract().getId(), event.getTransaction().getHash()));
            }
            else if (contractEvent instanceof FundsAddedEvent) {
                FundsAddedEvent fundsAddedEvent = (FundsAddedEvent) contractEvent;
                balanceProvider.getBalanceAsync(
                        event.getNetworkType(),
                        event.getContract().getAddress(),
                        event.getBlock().getNumber()
                )
                        .thenAccept(balance -> {
                            try {
                                externalNotifier.send(event.getNetworkType(), new FundsAddedNotify(
                                        event.getContract().getId(),
                                        event.getTransaction().getHash(),
                                        fundsAddedEvent.getAmount(),
                                        balance
                                ));
                            }
                            catch (Exception e) {
                                log.error("Sending notification failed.", e);
                            }

                            log.debug("Update balance in db for contract {} to {}.", event.getContract().getId(), balance);
                            try {
                                productRepository.updateBalance(event.getContract().getProduct().getId(), balance);
                            }
                            catch (Throwable e) {
                                log.error("Updating balance for contract {} failed.", event.getContract().getId(), e);
                            }
                        })
                        .exceptionally(throwable -> {
                            log.error("Getting balance for handling FundsAddedEvent failed.", throwable);
                            return null;
                        });
            }
            else if (contractEvent instanceof InvestEvent && event.getContract().getProduct() instanceof ProductInvestmentPool) {
                balanceProvider.getBalanceAsync(
                        event.getNetworkType(),
                        event.getContract().getAddress(),
                        event.getBlock().getNumber()
                )
                        .thenAccept(balance -> externalNotifier.send(event.getNetworkType(),
                                new ExFundsAddedNotify(
                                        event.getContract().getId(),
                                        event.getTransaction().getHash(),
                                        ((InvestEvent) contractEvent).getAmount(),
                                        balance,
                                        ((InvestEvent) contractEvent).getInvestorAddress()
                                )
                        ))
                        .exceptionally(throwable -> {
                            log.error("Getting balance for handling InvestEvent failed.", throwable);
                            return null;
                        });



            }
            else if (contractEvent instanceof WithdrawTokensEvent && event.getContract().getProduct() instanceof ProductInvestmentPool) {
                externalNotifier.send(event.getNetworkType(),
                        new TokensSentNotify(
                                event.getContract().getId(),
                                event.getTransaction().getHash(),
                                ((WithdrawTokensEvent) contractEvent).getInvestorAddress(),
                                ((WithdrawTokensEvent) contractEvent).getAmount()
                        ));
            }
            else if (contractEvent instanceof WithdrawRewardEvent && event.getContract().getProduct() instanceof ProductInvestmentPool) {
                externalNotifier.send(event.getNetworkType(),
                        new TokensSentNotify(
                                event.getContract().getId(),
                                event.getTransaction().getHash(),
                                ((WithdrawRewardEvent) contractEvent).getAdminAddress(),
                                ((WithdrawRewardEvent) contractEvent).getAmount()
                        ));
            }
            else if (contractEvent instanceof SetInvestmentAddressEvent && event.getContract().getProduct() instanceof ProductInvestmentPool) {
                externalNotifier.send(event.getNetworkType(),
                        new InvestmentPoolSetupNotify(
                                event.getContract().getId(),
                                event.getTransaction().getHash(),
                                ((SetInvestmentAddressEvent) contractEvent).getInvestmentAddress(),
                                null
                        ));
            }
            else if (contractEvent instanceof SetTokenAddressEvent && event.getContract().getProduct() instanceof ProductInvestmentPool) {
                externalNotifier.send(event.getNetworkType(),
                        new InvestmentPoolSetupNotify(
                                event.getContract().getId(),
                                event.getTransaction().getHash(),
                                null,
                                ((SetTokenAddressEvent) contractEvent).getTokenAddress()
                        ));
            }
            else if (contractEvent instanceof InitializedEvent) {
                externalNotifier.send(event.getNetworkType(),
                        new InitializedNotify(event.getContract().getId(), event.getTransaction().getHash()));
            }
            else if (contractEvent instanceof OwnershipTransferredEvent) {
                transferOwnershipHandler.handle(event.getNetworkType(), (OwnershipTransferredEvent) contractEvent, event.getTransactionReceipt());
            }
            else if (contractEvent instanceof FinalizedEvent || contractEvent instanceof MintFinishedEvent) {
                externalNotifier.send(event.getNetworkType(),
                        new FinalizedNotify(event.getContract().getId(), event.getTransaction().getHash()));
            }
            else if (contractEvent instanceof NotifiedEvent) {
                externalNotifier.send(event.getNetworkType(),
                        new NotifiedNotify(event.getContract().getId(), event.getTransaction().getHash()));
            }
            else if (contractEvent instanceof TimesChangedEvent) {
                externalNotifier.send(
                        event.getNetworkType(),
                        new TimesChangedNotify(
                                event.getContract().getId(),
                                event.getTransaction().getHash(),
                                ((TimesChangedEvent) contractEvent).getStartTime(),
                                ((TimesChangedEvent) contractEvent).getEndTime()
                        )
                );
            }
            else if (contractEvent instanceof WhitelistedAddressAddedEvent) {
                externalNotifier.send(
                        event.getNetworkType(),
                        new WhitelistAddedNotify(
                                event.getContract().getId(),
                                event.getTransaction().getHash(),
                                ((WhitelistedAddressAddedEvent) contractEvent).getWhitelistedAddress()
                        )
                );
            }
            else if (contractEvent instanceof WhitelistedAddressRemovedEvent) {
                externalNotifier.send(
                        event.getNetworkType(),
                        new WhitelistRemovedNotify(
                                event.getContract().getId(),
                                event.getTransaction().getHash(),
                                ((WhitelistedAddressRemovedEvent) contractEvent).getWhitelistedAddress()
                        )
                );
            }
            else if (contractEvent instanceof CancelledEvent) {
                externalNotifier.send(
                        event.getNetworkType(),
                        new CancelledNotify(
                                event.getContract().getId(),
                                PaymentStatus.COMMITTED,
                                event.getTransaction().getHash()
                        )
                );
            }
            else if (contractEvent instanceof ClaimRefundEvent) {
                externalNotifier.send(
                        event.getNetworkType(),
                        new RefundNotify(
                                event.getContract().getId(),
                                PaymentStatus.COMMITTED,
                                event.getTransaction().getHash(),
                                ((ClaimRefundEvent) contractEvent).getAmount()
                        )
                );
            }
        }
    }
}
