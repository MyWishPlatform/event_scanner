package io.lastwill.eventscan.repositories;

import io.lastwill.eventscan.model.Contract;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import java.util.Collection;
import java.util.List;

public interface ContractRepository extends CrudRepository<Contract, Integer> {
    List<Contract> findByAddressOrOwnerAddress(String address, String ownerAddress);

    @Query("select c from Contract c where c.address in (:addresses) or c.ownerAddress in (:addresses)")
    List<Contract> findByAddressesList(@Param("addresses") Collection<String> addresses);
}
