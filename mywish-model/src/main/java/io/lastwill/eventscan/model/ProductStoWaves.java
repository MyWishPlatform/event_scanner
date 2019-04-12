package io.lastwill.eventscan.model;

import lombok.Getter;

import javax.persistence.*;
import java.math.BigInteger;

@Entity
@Table(name = "contracts_contractdetailssto")
@PrimaryKeyJoinColumn(name = "contract_id")
@DiscriminatorValue("21")
@Getter
public class ProductStoWaves extends Product {
    @Override
    public int getContractType() {
        return 21;
    }

    @Override
    public BigInteger getCheckGasLimit() {
        return BigInteger.ZERO;
    }
}
