package io.lastwill.eventscan.model;

import lombok.Getter;

import javax.persistence.*;

@Entity
@Table(name = "contracts_contractdetailsgameassets")
@PrimaryKeyJoinColumn(name = "contract_id")
@DiscriminatorValue("16")
@Getter
public class ProductGameAssetsTron extends ProductTokenCommon {
    @Column(name = "token_name")
    private String name;

    @Column(name = "token_short_name")
    private String symbol;

    @Override
    public int getContractType() {
        return 16;
    }
}
