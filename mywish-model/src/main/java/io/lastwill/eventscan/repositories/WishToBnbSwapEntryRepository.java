package io.lastwill.eventscan.repositories;

import io.lastwill.eventscan.model.WishToBnbSwapEntry;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

public interface WishToBnbSwapEntryRepository extends CrudRepository<WishToBnbSwapEntry, Long> {
    @Transactional
    @Query("update WishToBnbSwapEntry e set e.bnbTxHash = :txHash")
    boolean setBnbTxHash(@Param("txHash") String txHash);

    @Query("select e from WishToBnbSwapEntry e where lower(e.ethTxHash) = lower(:ethTxHash)")
    WishToBnbSwapEntry findByEthTxHash(@Param("ethTxHash") String hash);
}
