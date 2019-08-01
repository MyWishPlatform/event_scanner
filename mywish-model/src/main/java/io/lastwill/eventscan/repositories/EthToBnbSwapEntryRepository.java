package io.lastwill.eventscan.repositories;

import io.lastwill.eventscan.model.EthToBnbSwapEntry;
import io.lastwill.eventscan.model.TransferStatus;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface EthToBnbSwapEntryRepository extends CrudRepository<EthToBnbSwapEntry, Long> {
    @Transactional
    @Query("update EthToBnbSwapEntry e set e.bnbTxHash = :txHash")
    boolean setBnbTxHash(@Param("txHash") String txHash);

    @Query("select e from EthToBnbSwapEntry e where lower(e.ethTxHash) = lower(:ethTxHash)")
    EthToBnbSwapEntry findByEthTxHash(@Param("ethTxHash") String hash);

    List<EthToBnbSwapEntry> findEthToBnbSwapEntriesByTransferStatus(@Param("transfer_status")TransferStatus status);

    boolean existsByTransferStatus(@Param("transfer_status") TransferStatus transferStatus);
}
