package io.lastwill.eventscan.model;

import lombok.Getter;

import javax.persistence.*;

@Entity
@Table(name = "contracts_contractdetailstrontoken")
@PrimaryKeyJoinColumn(name = "contract_id")
@DiscriminatorValue("15")
@Getter
public class ProductTokenTron extends ProductTokenCommon implements ProductNameable {
    @Column(name = "token_name")
    private String name;

    @Column(name = "token_short_name")
    private String symbol;

    @Override
    public int getContractType() {
        return 15;
    }
}
