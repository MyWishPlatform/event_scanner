package io.lastwill.eventscan.model;

import lombok.Getter;

import javax.persistence.*;
import java.math.BigInteger;
import java.time.ZonedDateTime;

@Entity
@Table(name = "contracts_contractdetailsswaps")
@PrimaryKeyJoinColumn(name = "contract_id")
@DiscriminatorValue("20")
@Getter
public class ProductSwaps extends Product {
    @Column(name = "owner_address")
    private String ownerAddress;
    @Column(name = "base_address")
    private String baseAddress;
    @Column(name = "base_limit")
    private BigInteger baseLimit;
    @Column(name = "quote_address")
    private String quoteAddress;
    @Column(name = "quote_limit")
    private BigInteger quoteLimit;
    @Column(name = "active_to")
    private ZonedDateTime activeTo;

    @Override
    public int getContractType() {
        return 20;
    }

    @Override
    public BigInteger getCheckGasLimit() {
        return BigInteger.ZERO;
    }
}
