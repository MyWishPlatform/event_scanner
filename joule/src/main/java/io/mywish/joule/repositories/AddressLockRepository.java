package io.mywish.joule.repositories;

import io.mywish.joule.model.AddressLock;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

public interface AddressLockRepository extends CrudRepository<AddressLock, Integer> {
    @Query("update AddressLock set lockedBy = :lockedBy where address = :address and lockedBy is null")
    @Modifying
    int updateLockedBy(@Param("address") String address, @Param("lockedBy") int lockedBy);

    @Query("update AddressLock set lockedBy = null where address = :address and lockedBy = :lockedBy")
    @Modifying
    int updateLockedByToNull(@Param("address") String address, @Param("lockedBy") int lockedBy);
}
