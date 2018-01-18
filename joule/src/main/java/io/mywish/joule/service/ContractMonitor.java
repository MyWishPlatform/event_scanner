package io.mywish.joule.service;

import io.lastwill.eventscan.events.ContractCreatedEvent;
import io.lastwill.eventscan.model.ProductDelayedPayment;
import io.lastwill.eventscan.repositories.ProductRepository;
import io.mywish.joule.model.JouleRegistration;
import io.mywish.joule.repositories.JouleRegistrationRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

@Slf4j
@Component
public class ContractMonitor {
    @Autowired
    private JouleRegistrationRepository jouleRegistrationRepository;
    @Autowired
    private ProductRepository repository;

    @EventListener
    public void onContractCreated(ContractCreatedEvent event) {
        if (!event.isSuccess()) {
            return;
        }

        if (event.getContract().getProduct() instanceof ProductDelayedPayment) {
            // TODO:
        }
    }
}
