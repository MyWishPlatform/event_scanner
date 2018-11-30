package io.lastwill.eventscan.repositories;

import io.lastwill.eventscan.model.LastBlock;
import io.lastwill.eventscan.model.NetworkType;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

public interface LastBlockRepository extends CrudRepository<LastBlock, Long> {
    @Query("select block.blockNumber from LastBlock block where block.network = :network")
    Long getLastBlockForNetwork(@Param("network") NetworkType network);

    @Modifying
    @Transactional
    @Query("update LastBlock lastBlock set lastBlock.blockNumber = :blockNumber where lastBlock.network = :network")
    void updateLastBlock(@Param("network") NetworkType network, @Param("blockNumber") Long blockNumber);
}
