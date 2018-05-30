package io.lastwill.eventscan.model;

import lombok.Getter;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.PrimaryKeyJoinColumn;
import javax.persistence.Table;
import java.math.BigInteger;

@Entity
@Table(name = "contracts_contractdetailsneo")
@PrimaryKeyJoinColumn(name = "contract_id")
@DiscriminatorValue("6")
@Getter
public class ProductTokenNeo extends ProductToken {
    @Override
    public int getContractType() {
        return 6;
    }
}
