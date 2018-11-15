package io.lastwill.eventscan.repositories;

import io.lastwill.eventscan.model.UserProfile;
import io.lastwill.eventscan.model.UserSiteBalance;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import java.util.Collection;
import java.util.List;

public interface UserSiteBalanceRepository extends CrudRepository<UserSiteBalance, Integer> {
    @Query("select c from UserProfile c where lower(c.internalAddress) in :addresses")
    List<UserSiteBalance> findByAddressesList(@Param("addresses") Collection<String> addresses);

    @Query("select c from UserProfile c where lower(c.internalAddress) = :address")
    UserSiteBalance findByInternalAddress(@Param("address") String internalAddress);

    @Query("select c from UserSiteBalance c where c.memo = :memo")
    UserSiteBalance findByMemo(@Param("memo") String memo);
}
