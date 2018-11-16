package io.mywish.airdrop.repositories;

import io.mywish.airdrop.model.AirdropEntry;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.NoRepositoryBean;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;
import java.util.stream.Stream;

@NoRepositoryBean
public interface AirdropEntryRepository<T extends AirdropEntry> extends CrudRepository<T, Integer> {
    @Query("select e from #{#entityName} e where e.inProcessing = false order by e.id asc ")
    Stream<T> findFirstNotProcessed();

    @Query("select e from #{#entityName} e where e.inProcessing = true and e.txHash in (:hashes)")
    List<T> findByTxHashes(@Param("hashes") Collection<String> hashes);

    @Query("select e from #{#entityName} e where e.inProcessing = true and e.txHash = :hash")
    List<T> findByTxHash(@Param("hash") String hash);

    @Transactional
    @Modifying
    @Query("update #{#entityName} e set e.inProcessing = true where e = :airdrop and e.inProcessing = false ")
    int process(@Param("airdrop") AirdropEntry airdropEntry);

    @Transactional
    @Modifying
    @Query("update #{#entityName} e set e.txHash = :txHash, e.eosishAmount = e.eosishAmount + :eosAmount, e.sentAt = :sentAt " +
            "where e = :airdrop and e.inProcessing = true ")
    int txSent(
            @Param("airdrop") AirdropEntry airdropEntry,
            @Param("txHash") String txHash,
            @Param("eosAmount") BigDecimal eosAmount,
            @Param("sentAt") LocalDateTime sentAt
    );

    @Transactional
    @Modifying
    @Query("update #{#entityName} e set e.blockNumber = :blockNumber " +
            "where e = :airdrop and e.inProcessing = true ")
    int txInBlock(
            @Param("airdrop") AirdropEntry airdropEntry,
            @Param("blockNumber") long blockNumber
    );
}
