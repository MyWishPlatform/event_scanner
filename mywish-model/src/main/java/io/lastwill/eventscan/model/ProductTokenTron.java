package io.lastwill.eventscan.model;

import lombok.Getter;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.PrimaryKeyJoinColumn;
import javax.persistence.Table;

@Entity
@Table(name = "contracts_contractdetailstrontoken")
@PrimaryKeyJoinColumn(name = "contract_id")
@DiscriminatorValue("15")
@Getter
public class ProductTokenTron extends ProductToken {
    @Override
    public int getContractType() {
        return 15;
    }
}
