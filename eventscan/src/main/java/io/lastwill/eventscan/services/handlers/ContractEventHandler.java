package io.lastwill.eventscan.services.handlers;

import io.lastwill.eventscan.events.ContractEventsEvent;
import io.lastwill.eventscan.events.contract.*;
import io.lastwill.eventscan.repositories.ProductRepository;
import io.lastwill.eventscan.services.BalanceProvider;
import io.lastwill.eventscan.services.EventParser;
import io.lastwill.eventscan.services.ExternalNotifier;
import io.lastwill.eventscan.services.handlers.events.TransferOwnershipHandler;
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
    private EventParser eventParser;

    @Autowired
    private ExternalNotifier externalNotifier;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private BalanceProvider balanceProvider;

    @Autowired
    private TransferOwnershipHandler transferOwnershipHandler;

    @EventListener
    public void eventsHandler(final ContractEventsEvent event) {
        if (externalNotifier == null) {
            return;
        }
        for (ContractEvent contractEvent: event.getEvents()) {
            if (contractEvent instanceof CheckedEvent) {
                externalNotifier.sendCheckedNotify(event.getContract(), event.getTransaction().getHash());
            }
            else if (contractEvent instanceof NeedRepeatCheckEvent) {
                externalNotifier.sendCheckRepeatNotify(event.getContract(), event.getTransaction().getHash());
            }
            else if (contractEvent instanceof KilledEvent) {
                externalNotifier.sendKilledNotification(event.getContract(), event.getTransaction().getHash());
            }
            else if (contractEvent instanceof TriggeredEvent) {
                externalNotifier.sendTriggeredNotification(event.getContract(), event.getTransaction().getHash());
            }
            else if (contractEvent instanceof FundsAddedEvent) {
                balanceProvider.getBalanceAsync(event.getContract().getAddress(), event.getBlock().getNumber().longValue())
                        .thenAccept(balance -> {
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
                externalNotifier.sendInitializedNotification(event.getContract(), event.getTransaction().getHash());
            }
            else if (contractEvent instanceof OwnershipTransferredEvent) {
                transferOwnershipHandler.handle((OwnershipTransferredEvent) contractEvent);
            }
            else if (contractEvent instanceof FinalizedEvent || contractEvent instanceof MintFinishedEvent) {
                externalNotifier.sendFinalizedNotification(event.getContract(), event.getTransaction().getHash());
            }
        }

    }
}
