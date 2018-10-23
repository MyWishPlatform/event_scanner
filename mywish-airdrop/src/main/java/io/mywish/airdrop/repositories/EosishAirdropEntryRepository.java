package io.mywish.airdrop.repositories;

import io.mywish.airdrop.model.EosishAirdropEntry;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;
import java.util.stream.Stream;

public interface EosishAirdropEntryRepository extends CrudRepository<EosishAirdropEntry, Integer> {
    @Query("select e from EosishAirdropEntry e where e.inProcessing = false order by e.wishAmount asc ")
    Stream<EosishAirdropEntry> findFirstNotProcessed();

    @Query("select e from EosishAirdropEntry e where e.inProcessing = true and e.txHash in (:hashes)")
    List<EosishAirdropEntry> findByTxHashes(@Param("hashes") Collection<String> hashes);

    @Query("select e from EosishAirdropEntry e where e.inProcessing = true and e.txHash = :hash")
    List<EosishAirdropEntry> findByTxHash(@Param("hash") String hash);

    @Transactional
    @Modifying
    @Query("update EosishAirdropEntry e set e.inProcessing = true where e = :airdrop and e.inProcessing = false ")
    int process(@Param("airdrop") EosishAirdropEntry airdropEntry);

    @Transactional
    @Modifying
    @Query("update EosishAirdropEntry e set e.txHash = :txHash, e.eosishAmount = :eosAmount, e.sentAt = :sentAt " +
            "where e = :airdrop and e.inProcessing = true ")
    int txSent(
            @Param("airdrop") EosishAirdropEntry airdropEntry,
            @Param("txHash") String txHash,
            @Param("eosAmount") BigDecimal eosAmount,
            @Param("sentAt") LocalDateTime sentAt
    );

    @Transactional
    @Modifying
    @Query("update EosishAirdropEntry e set e.blockNumber = :blockNumber " +
            "where e = :airdrop and e.inProcessing = true ")
    int txInBlock(
            @Param("airdrop") EosishAirdropEntry airdropEntry,
            @Param("blockNumber") long blockNumber
    );
}
