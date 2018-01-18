package io.mywish.joule.service;

import io.lastwill.eventscan.events.ContractCreatedEvent;
import io.lastwill.eventscan.model.Contract;
import io.lastwill.eventscan.model.ProductCrowdsale;
import io.mywish.joule.model.JouleRegistration;
import io.mywish.joule.repositories.JouleRegistrationRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.ZoneOffset;

@Slf4j
@Component
public class ContractCreationMonitor {
    @Autowired
    private JouleRegistrationRepository jouleRegistrationRepository;
//    @Autowired
//    private ProductRepository repository;

    @EventListener
    public void onContractCreated(ContractCreatedEvent event) {
        if (!event.isSuccess()) {
            return;
        }

        Contract contract = event.getContract();

        if (contract.getProduct() instanceof ProductCrowdsale) {
            ProductCrowdsale productCrowdsale = (ProductCrowdsale) contract.getProduct();
            jouleRegistrationRepository.save(new JouleRegistration(
                    contract,
                    LocalDateTime.ofInstant(productCrowdsale.getCheckDate(), ZoneOffset.UTC)));
        }
    }
}
