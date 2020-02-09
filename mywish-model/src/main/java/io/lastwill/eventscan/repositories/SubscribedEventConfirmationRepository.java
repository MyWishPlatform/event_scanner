package io.lastwill.eventscan.repositories;

import io.lastwill.eventscan.model.EventConfirmation;
import io.lastwill.eventscan.model.NetworkType;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.repository.CrudRepository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Set;

public interface SubscribedEventConfirmationRepository extends CrudRepository<EventConfirmation, Long> {

    @Override
    Set <EventConfirmation> findAll();

    Set <EventConfirmation> findAllByNetwork(NetworkType network);

    boolean existsByTxHash(String hash);

    @Transactional
    @Modifying
    void deleteByTxHash(String hash);

}
