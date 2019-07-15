package io.lastwill.eventscan.repositories;

import io.lastwill.eventscan.model.EthToBnbLinkEntry;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

public interface EthToBnbLinkEntryRepository extends CrudRepository<EthToBnbLinkEntry, Long> {
    boolean existsByEthAddressAndSymbol(@Param("ethAddress") String ethAddress, @Param("symbol") String symbol);

    EthToBnbLinkEntry findByEthAddress(@Param("ethAddress") String ethAddress);
}
