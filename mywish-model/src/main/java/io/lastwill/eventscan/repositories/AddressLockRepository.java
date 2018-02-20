package io.lastwill.eventscan.repositories;

import io.lastwill.eventscan.model.AddressLock;
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

    @Query("select c from AddressLock c where lower(c.address) in :addresses")
    List<AddressLock> findByAddressesList(@Param("addresses") Collection<String> addresses);
}
