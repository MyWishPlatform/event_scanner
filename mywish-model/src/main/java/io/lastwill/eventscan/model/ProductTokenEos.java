package io.lastwill.eventscan.model;

import lombok.Getter;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.PrimaryKeyJoinColumn;
import javax.persistence.Table;

@Entity
@Table(name = "contracts_contractdetailseostoken")
@PrimaryKeyJoinColumn(name = "contract_id")
@DiscriminatorValue("10")
@Getter
public class ProductTokenEos extends ProductToken {
    @Override
    public int getContractType() {
        return 10;
    }
}
