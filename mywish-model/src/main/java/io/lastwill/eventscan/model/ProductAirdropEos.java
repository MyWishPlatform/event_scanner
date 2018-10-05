package io.lastwill.eventscan.model;

import lombok.Getter;

import javax.persistence.*;
import java.math.BigInteger;

@Entity
@Table(name = "contracts_contractdetailseosairdrop")
@PrimaryKeyJoinColumn(name = "contract_id")
@DiscriminatorValue("13")
public class ProductAirdropEos extends Product {
    @Getter
    @Column(name = "admin_address")
    private String adminAddress;

    @Getter
    @Column(name = "token_address")
    private String tokenAddress;

    @Getter
    @Column(name = "token_short_name")
    private String tokenShortName;

    @Override
    public int getContractType() {
        return 13;
    }

    @Override
    public BigInteger getCheckGasLimit() {
        return BigInteger.valueOf(0);
    }
}
