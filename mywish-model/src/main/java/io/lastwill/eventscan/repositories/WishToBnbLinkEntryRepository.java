package io.lastwill.eventscan.repositories;

import io.lastwill.eventscan.model.WishToBnbLinkEntry;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

public interface WishToBnbLinkEntryRepository extends CrudRepository<WishToBnbLinkEntry, Long> {
    boolean existsByEthAddress(@Param("ethAddress") String ethAddress);

    WishToBnbLinkEntry findByEthAddress(@Param("ethAddress") String ethAddress);
}
