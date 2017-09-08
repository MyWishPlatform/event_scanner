package io.lastwill.eventscan.repositories;

import io.lastwill.eventscan.model.Contract;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigInteger;
import java.util.Collection;
import java.util.List;

public interface ContractRepository extends CrudRepository<Contract, Integer> {
    List<Contract> findByAddressOrOwnerAddress(String address, String ownerAddress);

    @Query("select c from Contract c where lower(c.address) in :addresses or lower(c.ownerAddress) in :addresses")
    List<Contract> findByAddressesList(@Param("addresses") Collection<String> addresses);

    @Transactional
    @Modifying
    @Query("update Contract set balance = :balanceWei where id = :id")
    void updateBalance(@Param("id") int id, @Param("balanceWei") BigInteger balanceWei);
}
