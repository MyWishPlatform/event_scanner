package io.lastwill.eventscan.repositories;

import io.lastwill.eventscan.model.EventConfirmation;
import io.lastwill.eventscan.model.NetworkType;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface EventConfirmationRepository extends CrudRepository<EventConfirmation, Long> {

    @Query("select c.hash, c.blockNumber, c.network from EventConfirmation c where c.network = :network")
    List<EventConfirmation> findAllByNetwork(@Param("network") NetworkType network);

    @Query("select c.blockNumber from EventConfirmation c where c.hash = :hash")
    boolean findByTxHash(String hash);

    @Query("select c from EventConfirmation c")
     List<EventConfirmation> findAll();

    @Transactional
    @Modifying
    boolean deleteByTxHash(String hash);

}
