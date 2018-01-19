package io.lastwill.eventscan.model;

import javax.persistence.*;
import java.math.BigInteger;
import java.time.ZonedDateTime;

@Entity
@Table(name = "contracts_contractdetailspizza")
@PrimaryKeyJoinColumn(name = "contract_id")
@DiscriminatorValue("3")
public class ProductPizza extends Product {
    @Override
    public int getContractType() {
        return 3;
    }

    @Override
    public BigInteger getCheckGasLimit() {
        return BigInteger.valueOf(200000);
    }

}
