package io.lastwill.eventscan.repositories;

import io.lastwill.eventscan.model.UserSiteBalance;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import java.util.Collection;
import java.util.List;

public interface UserSiteBalanceRepository extends CrudRepository<UserSiteBalance, Integer> {
    @Query("select c from UserSiteBalance c where c.ethAddress in :addresses")
    List<UserSiteBalance> findByEthAddressesList(@Param("addresses") Collection<String> addresses);

    @Query("select c from UserSiteBalance c where c.btcAddress in :addresses")
    List<UserSiteBalance> findByBtcAddressesList(@Param("addresses") Collection<String> addresses);

//    @Query("select c from UserSiteBalance c where c.tronAddress in :addresses")
//    List<UserSiteBalance> findByTronAddressesList(@Param("addresses") Collection<String> addresses);

    @Query("select c from UserSiteBalance c where c.ethAddress = :address")
    UserSiteBalance findByEthAddress(@Param("address") String internalAddress);

//    @Query("select c from UserSiteBalance c where c.tronAddress = :address")
//    UserSiteBalance findByTronAddress(@Param("address") String internalAddress);

    @Query("select c from UserSiteBalance c where c.memo = :memo")
    UserSiteBalance findByMemo(@Param("memo") String memo);
}
