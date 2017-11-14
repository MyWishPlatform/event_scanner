package io.mywish.dream.repositories;

import io.mywish.dream.model.Contract;
import org.springframework.data.repository.CrudRepository;

public interface ContractRepository extends CrudRepository<Contract, String> {
}
