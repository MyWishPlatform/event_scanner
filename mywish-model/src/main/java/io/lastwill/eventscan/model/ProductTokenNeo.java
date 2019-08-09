package io.lastwill.eventscan.model;

import lombok.Getter;

import javax.persistence.*;
import java.math.BigInteger;

@Entity
@Table(name = "contracts_contractdetailsneo")
@PrimaryKeyJoinColumn(name = "contract_id")
@DiscriminatorValue("6")
@Getter
public class ProductTokenNeo extends ProductTokenCommon {
    @Column(name = "token_name")
    private String name;

    @Column(name = "token_short_name")
    private String symbol;

    @Override
    public int getContractType() {
        return 6;
    }
}
