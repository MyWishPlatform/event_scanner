package io.lastwill.eventscan.model;

import lombok.Getter;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.PrimaryKeyJoinColumn;
import javax.persistence.Table;
import java.math.BigInteger;

@Entity
@Table(name = "contracts_contractdetailseosaccount")
@PrimaryKeyJoinColumn(name = "contract_id")
@DiscriminatorValue("11")
@Getter
public class ProductEosAccount extends Product {
    @Override
    public int getContractType() {
        return 11;
    }

    @Override
    public BigInteger getCheckGasLimit() {
        return BigInteger.ZERO;
    }
}
