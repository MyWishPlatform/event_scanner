package io.mywish.airdrop.model;

import lombok.Getter;

import javax.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@MappedSuperclass
@Getter
public abstract class AirdropEntry {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    @Column(nullable = false)
    private String eosAddress;
    @Column(nullable = false)
    private boolean inProcessing;
    private String txHash;
    private LocalDateTime sentAt;
    private Long blockNumber;
    private BigDecimal eosishAmount;
}
