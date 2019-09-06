package io.lastwill.eventscan.model;

import lombok.Getter;

import javax.persistence.*;

@Entity
@Table(name = "contracts_contractdetailseostoken")
@PrimaryKeyJoinColumn(name = "contract_id")
@DiscriminatorValue("10")
@Getter
public class ProductTokenEos extends ProductTokenCommon {
    @Column(name = "token_short_name")
    private String symbol;

    @Override
    public int getContractType() {
        return 10;
    }
}
