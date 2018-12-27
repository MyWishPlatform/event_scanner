package io.mywish.tron.blockchain.services;

import io.lastwill.eventscan.model.NetworkProviderType;
import io.mywish.blockchain.ContractEvent;
import io.mywish.tron.blockchain.builders.TronEventBuilder;
import io.mywish.tron.blockchain.model.WrapperTransactionTron;
import io.mywish.troncli4j.model.EventResult;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Component
public class WrapperLogTronService {
    @Autowired
    private List<TronEventBuilder<?>> builders = new ArrayList<>();
    private Map<String, TronEventBuilder<?>> buildersByName = new HashMap<>();

    @PostConstruct
    protected void init() throws Exception {
        for (TronEventBuilder<?> eventBuilder : builders) {
            if (eventBuilder.getNetworkProviderType() != NetworkProviderType.TRON) {
                return;
            }
            String key = eventBuilder.getDefinition().getName();
            if (buildersByName.containsKey(key)) {
                throw new Exception("Duplicate builder " + eventBuilder.getClass() + " with key " + key + ".");
            }

            buildersByName.put(key, eventBuilder);
            log.info("Add Tron event builder {} => {}.", key, eventBuilder.getClass().getSimpleName());
        }
    }

    public ContractEvent build(WrapperTransactionTron transaction, EventResult event) {
        TronEventBuilder<?> builder = buildersByName.get(event.getEventName());
        if (builder == null) {
            log.warn("There is no builder for Tron event {}.", event.getEventName());
            return null;
        }

        return builder.build(transaction.getSingleOutputAddress(), event);
    }
}
