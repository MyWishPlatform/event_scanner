package io.lastwill.eventscan.model;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.PrimaryKeyJoinColumn;
import javax.persistence.Table;
import java.math.BigInteger;

@Entity
@Table(name = "contracts_contractdetailsairdrop")
@PrimaryKeyJoinColumn(name = "contract_id")
@DiscriminatorValue("8")
public class ProductAirdrop extends Product {
    @Override
    public int getContractType() {
        return 8;
    }

    @Override
    public BigInteger getCheckGasLimit() {
        return BigInteger.valueOf(200000);
    }

}
