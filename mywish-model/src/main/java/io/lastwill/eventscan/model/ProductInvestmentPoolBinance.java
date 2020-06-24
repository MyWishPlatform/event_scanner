package io.lastwill.eventscan.model;

import lombok.Getter;

import javax.persistence.*;
import java.math.BigInteger;

@Entity
@Table(name = "contracts_contractdetailsbinanceinvestmentpool")
@PrimaryKeyJoinColumn(name = "contract_id")
@DiscriminatorValue("30")
public class ProductInvestmentPoolBinance extends Product {
    @Getter
    @Column(name = "admin_address")
    private String adminAddress;

    @Getter
    @Column(name = "token_address")
    private String tokenAddress;

    @Getter
    @Column(name = "investment_address")
    private String investmentAddress;

    @Override
    public int getContractType() {
        return 30;
    }

    @Override
    public BigInteger getCheckGasLimit() {
        return BigInteger.valueOf(3000000);
    }
}