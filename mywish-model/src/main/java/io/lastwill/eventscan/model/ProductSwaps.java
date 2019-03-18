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
    @Column(name = "owner_token_address")
    private String ownerTokenAddress;
    @Column(name = "owner_token_value")
    private BigInteger ownerTokenValue;
    @Column(name = "investor_token_address")
    private String investorTokenAddress;
    @Column(name = "investor_token_value")
    private BigInteger investorTokenValue;
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
