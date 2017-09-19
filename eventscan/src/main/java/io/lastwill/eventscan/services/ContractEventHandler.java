package io.lastwill.eventscan.services;

import io.lastwill.eventscan.events.ContractEventsEvent;
import io.lastwill.eventscan.model.EventValue;
import io.lastwill.eventscan.repositories.ContractRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class ContractEventHandler {
    @Autowired
    private EventParser eventParser;

    @Autowired
    private ExternalNotifier externalNotifier;

    @Autowired
    private ContractRepository contractRepository;

    @Autowired
    private BalanceProvider balanceProvider;

    @EventListener
    public void eventsHandler(final ContractEventsEvent event) {
        for (EventValue values: event.getEvents()) {
            if (eventParser.Checked == values.getEvent()) {
                externalNotifier.sendCheckedNotify(event.getContract(), event.getTransaction().getHash());
            }
            else if (eventParser.NeedRepeatCheck == values.getEvent()) {
                externalNotifier.sendCheckRepeatNotify(event.getContract());
            }
            else if (eventParser.Killed == values.getEvent()) {
                externalNotifier.sendKilledNotification(event.getContract());
            }
            else if (eventParser.FundsAdded == values.getEvent()) {
                balanceProvider.getBalanceAsync(event.getContract().getAddress(), event.getBlock().getNumber().longValue())
                        .thenAccept(balance -> {
                            log.debug("Update balance in db for contract {} to {}.", event.getContract().getId(), balance);
                            try {
                                contractRepository.updateBalance(event.getContract().getId(), balance);
                            }
                            catch (Throwable e) {
                                log.error("Updating balance for contract {} failed.", event.getContract().getId(), e);
                            }
                        });
            }
        }

    }
}
