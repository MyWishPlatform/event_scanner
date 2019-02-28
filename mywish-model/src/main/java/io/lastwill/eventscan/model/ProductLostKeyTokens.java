package io.lastwill.eventscan.model;

import lombok.Getter;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.PrimaryKeyJoinColumn;
import javax.persistence.Table;

@Entity
@Table(name = "contracts_contractdetailslostkeytokens")
@PrimaryKeyJoinColumn(name = "contract_id")
@DiscriminatorValue("19")
@Getter
public class ProductLostKeyTokens extends ProductToken {
    @Override
    public int getContractType() {
        return 19;
    }
}
