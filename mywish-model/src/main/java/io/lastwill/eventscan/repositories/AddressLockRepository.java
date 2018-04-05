package io.lastwill.eventscan.repositories;

import io.lastwill.eventscan.model.AddressLock;
import io.mywish.scanner.model.NetworkType;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;
import java.util.List;

public interface AddressLockRepository extends CrudRepository<AddressLock, Integer> {
    @Transactional
    @Query("update AddressLock set lockedBy = :lockedBy where address = :address and lockedBy is null")
    @Modifying
    int updateLockedBy(@Param("address") String address, @Param("lockedBy") int lockedBy);

    @Transactional
    @Query("update AddressLock set lockedBy = null where address = :address and lockedBy = :lockedBy")
    @Modifying
    int updateLockedByToNull(@Param("address") String address, @Param("lockedBy") int lockedBy);

    @Query("select c from AddressLock c where c.network.type = :networkType and lower(c.address) in :addresses")
    List<AddressLock> findByAddressesList(@Param("networkType") NetworkType networkType, @Param("addresses") Collection<String> addresses);
}
