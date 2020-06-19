package io.lastwill.eventscan.model;

import lombok.Getter;

import javax.persistence.*;

@Entity
@Table(name = "contracts_contractdetailstoken")
@PrimaryKeyJoinColumn(name = "contract_id")
@DiscriminatorValue("28")
@Getter
public class ProductTokenBinance extends ProductTokenCommon implements ProductNameable {
    @Column(name = "token_name")
    private String name;

    @Column(name = "token_short_name")
    private String symbol;

    @Override
    public int getContractType() {
        return 28;
    }
}
