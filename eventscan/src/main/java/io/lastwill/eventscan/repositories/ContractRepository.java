package io.lastwill.eventscan.repositories;

import io.lastwill.eventscan.model.Contract;
import io.lastwill.eventscan.model.Product;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigInteger;
import java.util.Collection;
import java.util.List;

public interface ContractRepository extends CrudRepository<Contract, Integer> {

    @Query("select c from Contract c where lower(c.address) in :addresses")
    List<Contract> findByAddressesList(@Param("addresses") Collection<String> addresses);

    @Query("select c from Contract c where c.product = :product and lower(c.txHash) = :txHash")
    List<Contract> findByProductAndTxHash(@Param("product") Product product, @Param("txHash") String txHash);
}
