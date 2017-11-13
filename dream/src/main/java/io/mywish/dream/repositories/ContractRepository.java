package io.mywish.dream.repositories;

import io.mywish.dream.exception.DuplicateContractException;
import io.mywish.dream.model.Contract;
import org.springframework.stereotype.Component;

import java.util.concurrent.ConcurrentHashMap;

@Component
public class ContractRepository {
    private final ConcurrentHashMap<String, Contract> contracts = new ConcurrentHashMap<>();

    public void save(Contract contract) {
        Contract previous = contracts.putIfAbsent(contract.getAddress(), contract);
        if (previous != null) {
            throw new DuplicateContractException("Contract " + contract.getAddress() + " already exists.");
        }
    }
}
