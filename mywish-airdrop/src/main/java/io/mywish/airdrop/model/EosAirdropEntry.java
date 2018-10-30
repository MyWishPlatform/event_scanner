package io.mywish.airdrop.model;

import lombok.Getter;

import javax.persistence.Entity;
import javax.persistence.Table;
import java.math.BigDecimal;

@Getter
@Entity
@Table(name = "eos_airdrop")
public class EosAirdropEntry extends AirdropEntry {
    private BigDecimal eosAmount;
}
