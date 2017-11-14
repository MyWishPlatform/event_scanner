package io.mywish.dream.service.cache;

import io.mywish.dream.model.contracts.TicketSale;
import org.springframework.stereotype.Component;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;

@Component
public class ContractCache {
    private final ConcurrentHashMap<String, TicketSale> contracts = new ConcurrentHashMap<>();

    public TicketSale tryGet(String key, Function<String, TicketSale> supplier) {
        return contracts.computeIfAbsent(key, supplier);
    }

    public CompletionStage<TicketSale> tryGetAsync(String key, Function<String, CompletionStage<TicketSale>> supplier) {
        if (contracts.containsKey(key)) {
            return CompletableFuture.completedFuture(contracts.get(key));
        }
        else {
            return supplier.apply(key);
        }
    }

    public void put(TicketSale ticketSale) {
        contracts.put(ticketSale.getContractAddress(), ticketSale);
    }
}
