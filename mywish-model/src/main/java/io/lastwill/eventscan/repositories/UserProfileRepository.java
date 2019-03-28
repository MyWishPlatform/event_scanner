package io.lastwill.eventscan.repositories;

import io.lastwill.eventscan.model.User;
import io.lastwill.eventscan.model.UserProfile;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import java.util.Collection;
import java.util.List;

public interface UserProfileRepository extends CrudRepository<UserProfile, Integer> {
    @Query("select c from UserProfile c where lower(c.internalAddress) in :addresses")
    List<UserProfile> findByAddressesList(@Param("addresses") Collection<String> addresses);

    @Query("select c from UserProfile c where lower(c.internalAddress) = :address")
    UserProfile findByInternalAddress(@Param("address") String internalAddress);

    @Query("select c from UserProfile c where c.memo = :memo")
    UserProfile findByMemo(@Param("memo") String memo);

    UserProfile findByUser(@Param("user") User user);
}
