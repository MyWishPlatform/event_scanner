package io.lastwill.eventscan.services;

import io.lastwill.eventscan.events.ContractEventsEvent;
import io.lastwill.eventscan.model.EventValue;
import io.lastwill.eventscan.repositories.ProductRepository;
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

    @EventListener
    public void eventsHandler(final ContractEventsEvent event) {
        if (externalNotifier == null) {
            return;
        }
        for (EventValue values: event.getEvents()) {
            if (eventParser.Checked == values.getEvent()) {
                externalNotifier.sendCheckedNotify(event.getContract(), event.getTransaction().getHash());
            }
            else if (eventParser.NeedRepeatCheck == values.getEvent()) {
                externalNotifier.sendCheckRepeatNotify(event.getContract(), event.getTransaction().getHash());
            }
            else if (eventParser.Killed == values.getEvent()) {
                externalNotifier.sendKilledNotification(event.getContract(), event.getTransaction().getHash());
            }
            else if (eventParser.Triggered == values.getEvent()) {
                externalNotifier.sendTriggeredNotification(event.getContract(), event.getTransaction().getHash());
            }
            else if (eventParser.FundsAdded == values.getEvent()) {
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
            else if (eventParser.Initialized == values.getEvent()) {
                externalNotifier.sendInitializedNotification(event.getContract(), event.getTransaction().getHash());
                // ignore all other
                return;
            }
            else if (eventParser.OwnershipTransferred == values.getEvent()) {
                externalNotifier.sendOwnershipTransferredNotification(event.getContract(), event.getTransaction().getHash());
            }
        }

    }
}
