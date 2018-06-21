package io.lastwill.eventscan.services.handlers;

import io.lastwill.eventscan.events.model.ContractEventsEvent;
import io.lastwill.eventscan.events.model.contract.*;
import io.lastwill.eventscan.events.model.contract.crowdsale.FinalizedEvent;
import io.lastwill.eventscan.events.model.contract.crowdsale.TimesChangedEvent;
import io.lastwill.eventscan.events.model.contract.crowdsale.WhitelistedAddressAddedEvent;
import io.lastwill.eventscan.events.model.contract.crowdsale.WhitelistedAddressRemovedEvent;
import io.lastwill.eventscan.messages.*;
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
        for (ContractEvent contractEvent: event.getEvents()) {
            if (contractEvent instanceof CheckedEvent) {
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
                        });
            }
            else if (contractEvent instanceof InitializedEvent) {
                externalNotifier.send(event.getNetworkType(),
                        new InitializedNotify(event.getContract().getId(), event.getTransaction().getHash()));
            }
            else if (contractEvent instanceof OwnershipTransferredEvent) {
                transferOwnershipHandler.handle(event.getNetworkType(), (OwnershipTransferredEvent) contractEvent);
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
        }
    }
}
