package io.mywish.airdrop.model;

import lombok.Getter;

import javax.persistence.Entity;
import javax.persistence.Table;
import java.math.BigDecimal;

@Getter
@Entity
@Table(name = "bounty_airdrop")
public class BountyAirdropEntry extends AirdropEntry {
    private BigDecimal bonusAmount;
}
