package io.lastwill.eventscan.repositories;

import io.lastwill.eventscan.model.Contract;
import io.lastwill.eventscan.model.NetworkType;
import io.lastwill.eventscan.model.Product;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import java.util.Collection;
import java.util.List;

public interface ContractRepository extends CrudRepository<Contract, Integer> {

    @Query("select c from Contract c where c.product.network.type = :network and lower(c.address) in :addresses")
    List<Contract> findByAddressesList(@Param("addresses") Collection<String> addresses, @Param("network") NetworkType network);

    @Query("select c from Contract c where c.product = :product")
    List<Contract> findByProduct(@Param("product") Product product);

    @Query("select c from Contract c where lower(c.txHash) = :txHash")
    List<Contract> findByTxHash(@Param("txHash") String txHash);

    @Query("select c from Contract c where c.product.network.type = :network and lower(c.txHash) in :hashes")
    List<Contract> findByTxHashes(@Param("hashes") Collection<String> hashes, @Param("network") NetworkType network);
}
