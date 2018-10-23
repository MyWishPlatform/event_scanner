package io.mywish.airdrop.model;

import lombok.Getter;

import javax.persistence.*;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.time.LocalDateTime;

@Entity
@Getter
@Table(name = "eosish_airdrop")
public class EosishAirdropEntry {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    @Column(nullable = false)
    private String ethAddress;
    @Column(nullable = false)
    private String eosAddress;
    @Column(nullable = false)
    private BigInteger wishAmount;
    private BigDecimal eosishAmount;
    @Column(nullable = false)
    private boolean inProcessing;
    private String txHash;
    private LocalDateTime sentAt;
    private Long blockNumber;

}
