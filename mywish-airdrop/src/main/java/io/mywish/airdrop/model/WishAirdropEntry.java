package io.mywish.airdrop.model;

import lombok.Getter;

import javax.persistence.*;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.time.LocalDateTime;

@Entity
@Getter
@Table(name = "eosish_airdrop")
public class WishAirdropEntry extends AirdropEntry {
    @Column(nullable = false)
    private String ethAddress;
    @Column(nullable = false)
    private BigInteger wishAmount;
}
