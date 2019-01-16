package io.lastwill.eventscan.model;

import lombok.Getter;

import javax.persistence.*;
import java.math.BigInteger;

@Entity
@Table(name = "contracts_contractdetailstronairdrop")
@PrimaryKeyJoinColumn(name = "contract_id")
@DiscriminatorValue("17")
public class ProductAirdropTron extends Product {
    @Getter
    @Column(name = "admin_address")
    private String adminAddress;

    @Getter
    @Column(name = "token_address")
    private String tokenAddress;

    @Override
    public int getContractType() {
        return 17;
    }

    @Override
    public BigInteger getCheckGasLimit() {
        return BigInteger.valueOf(0);
    }
}
