package io.lastwill.eventscan.model;

import lombok.Getter;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.PrimaryKeyJoinColumn;
import javax.persistence.Table;

@Entity
@Table(name = "contracts_contractdetailsgameassets")
@PrimaryKeyJoinColumn(name = "contract_id")
@DiscriminatorValue("16")
@Getter
public class ProductGameAssetsTron extends ProductToken {
    @Override
    public int getContractType() {
        return 16;
    }
}
