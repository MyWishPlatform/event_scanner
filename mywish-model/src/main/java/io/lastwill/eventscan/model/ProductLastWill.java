package io.lastwill.eventscan.model;

import javax.persistence.*;
import java.math.BigInteger;
import java.time.ZonedDateTime;

@Entity
@Table(name = "contracts_contractdetailslastwill")
@PrimaryKeyJoinColumn(name = "contract_id")
@DiscriminatorValue("0")
public class ProductLastWill extends ProductCheckable {
    @Override
    public int getContractType() {
        return 0;
    }

    @Override
    public BigInteger getCheckGasLimit() {
        return BigInteger.valueOf(200000);
    }
}
